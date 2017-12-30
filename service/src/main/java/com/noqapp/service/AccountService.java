package com.noqapp.service;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.domain.ForgotRecoverEntity;
import com.noqapp.domain.InviteEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserAuthenticationEntity;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.types.AccountInactiveReasonEnum;
import com.noqapp.domain.types.RoleEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.repository.ForgotRecoverManager;
import com.noqapp.repository.UserAccountManager;
import com.noqapp.repository.UserAuthenticationManager;
import com.noqapp.repository.UserPreferenceManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.HashText;
import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.service.exceptions.DuplicateAccountException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * User: hitender
 * Date: 11/19/16 12:45 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class AccountService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private UserAccountManager userAccountManager;
    private UserAuthenticationManager userAuthenticationManager;
    private UserPreferenceManager userPreferenceManager;
    private UserProfileManager userProfileManager;
    private GenerateUserIdService generateUserIdService;
    private EmailValidateService emailValidateService;
    private InviteService inviteService;
    private ForgotRecoverManager forgotRecoverManager;

    private ExecutorService executorService;

    @Autowired
    public AccountService(
            UserAccountManager userAccountManager,
            UserAuthenticationManager userAuthenticationManager,
            UserPreferenceManager userPreferenceManager,
            UserProfileManager userProfileManager,
            GenerateUserIdService generateUserIdService,
            EmailValidateService emailValidateService,
            InviteService inviteService,
            ForgotRecoverManager forgotRecoverManager
    ) {
        this.userAccountManager = userAccountManager;
        this.userAuthenticationManager = userAuthenticationManager;
        this.userPreferenceManager = userPreferenceManager;
        this.userProfileManager = userProfileManager;
        this.generateUserIdService = generateUserIdService;
        this.emailValidateService = emailValidateService;
        this.inviteService = inviteService;
        this.forgotRecoverManager = forgotRecoverManager;

        this.executorService = newCachedThreadPool();
    }

    public UserProfileEntity doesUserExists(String mail) {
        return userProfileManager.findOneByMail(mail);
    }

    public UserProfileEntity checkUserExistsByPhone(String phone) {
        return userProfileManager.findOneByPhone(phone);
    }

    public UserAccountEntity findByQueueUserId(String qid) {
        return userAccountManager.findByQueueUserId(qid);
    }

    @Mobile
    public UserAccountEntity findByUserId(String userId) {
        return userAccountManager.findByUserId(userId);
    }

    public void save(UserAccountEntity userAccount) {
        try {
            userAccountManager.save(userAccount);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserAccountEntity={}", e.getLocalizedMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error("Saving UserAccount qid={} reason={}", userAccount.getQueueUserId(), e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving user account");
        }
    }

    public void saveUserAccount(UserAccountEntity userAccount) {
        userAccountManager.save(userAccount);
    }

    /**
     * Creates new user for client or merchant account. There are some rollback but this process should not fail.
     *
     * @param phone
     * @param firstName
     * @param lastName
     * @param mail
     * @param birthday
     * @param gender
     * @param countryShortName
     * @param timeZone
     * @param password
     * @param inviteCode
     * @return
     */
    public UserAccountEntity createNewAccount(
            String phone,
            String firstName,
            String lastName,
            String mail,
            String birthday,
            String gender,
            String countryShortName,
            String timeZone,
            String password,
            String inviteCode,
            boolean phoneValidated
    ) {
        String phoneWithCountryCode = Formatter.phoneCleanup(phone);
        String phoneRaw = Formatter.phoneStripCountryCode("+" + phoneWithCountryCode);
        String firstNameCleanedUp = WordUtils.capitalizeFully(firstName);
        String lastNameCleanedUp = WordUtils.capitalizeFully(lastName);
        LOG.debug("Check by phoneWithCountryCode={} phoneRaw={}", phoneWithCountryCode, phoneRaw);
        if (null == checkUserExistsByPhone(phoneWithCountryCode) && null == doesUserExists(mail)) {
            UserAccountEntity userAccount = null;
            UserProfileEntity userProfile;

            String qid = generateUserIdService.getNextAutoGeneratedUserId();
            try {
                userAccount = UserAccountEntity.newInstance(
                        qid,
                        mail,
                        firstNameCleanedUp,
                        lastNameCleanedUp
                );
                userAccount.setAccountValidated(false);
                userAccount.setAccountValidatedBeginDate();
                userAccount.setPhoneValidated(phoneValidated);
                userAccount.active();

                /* Login through Mobile. */
                if (null == password) {
                    UserAuthenticationEntity userAuthentication = generateUserAuthentication(null);
                    userAccount.setUserAuthentication(userAuthentication);
                }
                userAccountManager.save(userAccount);
                /* Set authentication. This will speed up login through browser */
                if (null != password) {
                    executorService.submit(() -> createAuthentication(password, qid));
                }

                if (StringUtils.isBlank(mail)) {
                    mail = RandomString.generateEmailAddressWithDomain(new ScrubbedInput(firstNameCleanedUp), new ScrubbedInput(lastNameCleanedUp), qid);
                    userAccount.setUserId(mail);
                    userAccountManager.save(userAccount);
                }
                LOG.info("UserAccount created{}", userAccount);

                userProfile = UserProfileEntity.newInstance(
                        mail,
                        firstNameCleanedUp,
                        lastNameCleanedUp,
                        qid,
                        birthday
                );
                userProfile.setPhone(phoneWithCountryCode);
                userProfile.setPhoneRaw(phoneRaw);
                userProfile.setGender(gender);
                userProfile.setCountryShortName(countryShortName);
                userProfile.setTimeZone(timeZone);
                String generatedInviteCode = RandomString.generateInviteCode(firstNameCleanedUp, lastNameCleanedUp, qid);
                while (null != userProfileManager.inviteCodeExists(generatedInviteCode)) {
                    generatedInviteCode = RandomString.generateInviteCode(firstNameCleanedUp, lastNameCleanedUp, qid);
                }
                userProfile.setInviteCode(generatedInviteCode);
                userProfileManager.save(userProfile);
            } catch (Exception e) {
                LOG.error("During saving UserProfileEntity={}", e.getLocalizedMessage(), e);

                //Roll back
                UserAuthenticationEntity userAuthentication = null;
                if (null != userAccount) {
                    userAccount = findByQueueUserId(userAccount.getQueueUserId());
                    userAuthentication = userAccount.getUserAuthentication();
                    userAccountManager.deleteHard(userAccount);
                }

                if (null != userAuthentication) {
                    userAuthenticationManager.deleteHard(userAuthentication);
                }
                throw new RuntimeException("Error saving user profile");
            }

            createPreferences(userProfile);
            if (StringUtils.isNotBlank(inviteCode)) {
                UserProfileEntity userProfileOfInvitee = findProfileByInviteCode(inviteCode);
                if (null != userProfileOfInvitee) {
                    InviteEntity invite = new InviteEntity(qid, userProfileOfInvitee.getQueueUserId(), inviteCode);
                    inviteService.save(invite);
                } else {
                    InviteEntity invite = new InviteEntity(qid, null, null);
                    inviteService.save(invite);
                }
            } else {
                InviteEntity invite = new InviteEntity(qid, null, null);
                inviteService.save(invite);
            }
            return userAccount;
        } else {
            LOG.error("Account creation failed as it already exists for phone={} mail={}", phoneWithCountryCode, mail);
            throw new DuplicateAccountException("Account with credential already exists");
        }
    }

    @Mobile
    public UserProfileEntity findProfileByInviteCode(String inviteCode) {
        return userProfileManager.inviteCodeExists(inviteCode);
    }

    /**
     * Save userProfile.
     */
    public void save(UserProfileEntity userProfile) {
        try {
            userProfileManager.save(userProfile);
            LOG.debug("Saved UserProfile qid={} id={}", userProfile.getQueueUserId(), userProfile.getId());
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserProfileEntity={}", e.getLocalizedMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error("Saving UserProfile qid={} reason={}", userProfile.getQueueUserId(), e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving user profile");
        }
    }

    private UserAuthenticationEntity generateUserAuthentication(String password) {
        UserAuthenticationEntity userAuthentication;
        try {
            if (StringUtils.isBlank(password)) {
                userAuthentication = getUserAuthenticationEntity();
            } else {
                userAuthentication = getUserAuthenticationEntity(password);
            }
        } catch (Exception e) {
            LOG.error("During saving UserAuthenticationEntity={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("error saving user authentication ", e);
        }
        return userAuthentication;
    }

    private UserAuthenticationEntity getUserAuthenticationEntity(String password) {
        UserAuthenticationEntity userAuthentication = UserAuthenticationEntity.newInstance(
                HashText.computeSCrypt(password),
                HashText.computeBCrypt(RandomString.newInstance().nextString())
        );
        userAuthenticationManager.save(userAuthentication);
        return userAuthentication;
    }

    /**
     * Use for Social signup or for invite. This should speed up the sign up process as it eliminates dual creation
     * of BCrypt string.
     *
     * @return
     */
    public UserAuthenticationEntity getUserAuthenticationEntity() {
        UserAuthenticationEntity userAuthentication = UserAuthenticationEntity.newInstance(
                HashText.computeSCrypt(RandomString.newInstance().nextString()),
                HashText.computeBCrypt(RandomString.newInstance().nextString())
        );
        userAuthenticationManager.save(userAuthentication);
        return userAuthentication;
    }

    /**
     * Create and Save user preferences. Shared with social registration.
     */
    public void createPreferences(UserProfileEntity userProfile) {
        try {
            UserPreferenceEntity userPreferenceEntity = UserPreferenceEntity.newInstance(userProfile);
            userPreferenceManager.save(userPreferenceEntity);
            LOG.debug("Created UserPreferenceEntity={}", userPreferenceEntity.getQueueUserId());
        } catch (Exception e) {
            LOG.error("Saving UserPreferenceEntity qid={} reason={}", userProfile.getQueueUserId(), e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving user preference");
        }
    }

    /**
     * Should be called when from catch condition of DataIntegrityViolationException.
     *
     * @param userAccount
     * @param e
     */
    public void deleteAllWhenAccountCreationFailedDueToDuplicate(UserAccountEntity userAccount, DataIntegrityViolationException e) {
        Assert.notNull(e, "DataIntegrityViolationException is not set or not invoked properly");

        userAuthenticationManager.deleteHard(userAccount.getUserAuthentication());
        userAccountManager.deleteHard(userAccount);

        UserPreferenceEntity userPreference = userPreferenceManager.getByQueueUserId(userAccount.getQueueUserId());
        if (null != userPreference) {
            userPreferenceManager.deleteHard(userPreference);
        }

        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(userAccount.getQueueUserId());
        if (null != userProfile) {
            userProfileManager.deleteHard(userProfile);
        }
    }

    public UserProfileEntity findProfileByQueueUserId(String qid) {
        return userProfileManager.findByQueueUserId(qid);
    }

    private void updateName(String firstName, String lastName, String qid) {
        UserAccountEntity userAccount = findByQueueUserId(qid);
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);

        userAccount.setFirstName(firstName);
        userAccount.setLastName(lastName);
        userAccount.setDisplayName(userAccount.getName());

        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);

        userProfileManager.save(userProfile);
        userAccountManager.save(userAccount);
    }

    public void validateAccount(EmailValidateEntity emailValidate, UserAccountEntity userAccount) {
        markAccountValidated(userAccount);

        emailValidate.inActive();
        emailValidateService.saveEmailValidateEntity(emailValidate);
    }

    private void markAccountValidated(UserAccountEntity userAccount) {
        if (null != userAccount.getAccountInactiveReason()) {
            switch (userAccount.getAccountInactiveReason()) {
                case ANV:
                    updateAccountToValidated(userAccount.getId(), AccountInactiveReasonEnum.ANV);
                    break;
                default:
                    LOG.error("Reached unreachable condition, qid={}", userAccount.getQueueUserId());
                    throw new RuntimeException("Reached unreachable condition " + userAccount.getQueueUserId());
            }
        } else {
            userAccount.setAccountValidated(true);
            saveUserAccount(userAccount);
        }
    }

    private void updateAccountToValidated(String id, AccountInactiveReasonEnum accountInactiveReason) {
        userAccountManager.updateAccountToValidated(id, accountInactiveReason);
    }

    /**
     * Change user role to match user level.
     *
     * @param qid
     * @param userLevel
     * @return
     */
    public UserAccountEntity changeAccountRolesToMatchUserLevel(String qid, UserLevelEnum userLevel) {
        UserAccountEntity userAccount = findByQueueUserId(qid);
        Set<RoleEnum> roles = new LinkedHashSet<>();
        switch (userLevel) {
            case TECHNICIAN:
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                userAccount.setRoles(roles);
                break;
            case SUPERVISOR:
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                roles.add(RoleEnum.ROLE_SUPERVISOR);
                userAccount.setRoles(roles);
                break;
            case ADMIN:
                /* As of now admin does not have any Merchant role. */
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                roles.add(RoleEnum.ROLE_SUPERVISOR);
                roles.add(RoleEnum.ROLE_ADMIN);
                roles.add(RoleEnum.ROLE_ANALYSIS);
                userAccount.setRoles(roles);
                break;
            case ANALYSIS:
                roles.add(RoleEnum.ROLE_ANALYSIS);
                userAccount.setRoles(roles);
                break;
            case CLIENT:
                roles.add(RoleEnum.ROLE_CLIENT);
                userAccount.setRoles(roles);
                break;
            case Q_SUPERVISOR:
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_Q_SUPERVISOR);
                userAccount.setRoles(roles);
                break;
            case S_MANAGER:
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_Q_SUPERVISOR);
                roles.add(RoleEnum.ROLE_S_MANAGER);
                userAccount.setRoles(roles);
                break;
            case M_ADMIN:
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_Q_SUPERVISOR);
                roles.add(RoleEnum.ROLE_S_MANAGER);
                roles.add(RoleEnum.ROLE_M_ADMIN);
                userAccount.setRoles(roles);
                break;
            default:
                LOG.error("Reached unreachable condition, UserLevel={}", userLevel.name());
                throw new RuntimeException("Reached unreachable condition " + userLevel.name());
        }
        return userAccount;
    }

    /**
     * Updates existing userId with new userId.
     * </p>
     * Do not add send email in this method. Any call invokes this method needs to call accountValidationMail after it.
     *
     * @param existingUserId
     * @param newUserId
     * @return
     * @see com.noqapp.service.MailService#accountValidationMail(String, String, String) ()
     */
    @Mobile
    @SuppressWarnings ("unused")
    public UserAccountEntity updateUID(String existingUserId, String newUserId) {
        UserAccountEntity userAccount = findByUserId(existingUserId);
        if (!userAccount.isAccountValidated()) {
            emailValidateService.invalidateAllEntries(userAccount.getQueueUserId());
        }
        userAccount.setUserId(newUserId);
        userAccount.setAccountValidated(false);
        userAccount.active();

        UserProfileEntity userProfile = doesUserExists(existingUserId);
        userProfile.setEmail(newUserId);

        /* Always update userAccount before userProfile. */
        userAccountManager.save(userAccount);
        userProfileManager.save(userProfile);

        return userAccount;
    }

    /**
     * Used in for sending authentication link to recover account in case of the lost password.
     *
     * @param queueUserId
     * @return
     */
    ForgotRecoverEntity initiateAccountRecovery(String queueUserId) {
        String authenticationKey = HashText.computeBCrypt(RandomString.newInstance().nextString());
        ForgotRecoverEntity forgotRecoverEntity = ForgotRecoverEntity.newInstance(queueUserId, authenticationKey);
        forgotRecoverManager.save(forgotRecoverEntity);
        return forgotRecoverEntity;
    }

    public void invalidateAllEntries(String queueUserId) {
        forgotRecoverManager.invalidateAllEntries(queueUserId);
    }

    public ForgotRecoverEntity findByAuthenticationKey(String key) {
        return forgotRecoverManager.findByAuthenticationKey(key);
    }

    /**
     * Called during forgotten password or during an invite.
     *
     * @param userAuthentication
     */
    public void updateAuthentication(UserAuthenticationEntity userAuthentication) {
        userAuthenticationManager.save(userAuthentication);
    }

    /**
     * Update user profile info.
     *
     * @param registerUser
     * @param username
     */
    public void updateUserProfile(RegisterUser registerUser, String username) {
        UserProfileEntity userProfile;
        if (!registerUser.getEmail().equalsIgnoreCase(username)) {
            updateUID(username, registerUser.getEmail());
            userProfile = doesUserExists(registerUser.getEmail());
            registerUser.setEmailValidated(false);
        } else {
            userProfile = doesUserExists(username);
        }

        userProfile.setAddress(registerUser.getAddress());
        userProfile.setCountryShortName(registerUser.getCountryShortName());
        userProfile.setPhone(registerUser.getPhoneWithCountryCode());
        userProfile.setPhoneRaw(registerUser.getPhoneNotFormatted());
        userProfile.setTimeZone(registerUser.getTimeZone());
        userProfile.setBirthday(registerUser.getBirthday());
        userProfile.setAddressOrigin(registerUser.getAddressOrigin());
        save(userProfile);

        if (!userProfile.getFirstName().equals(registerUser.getFirstName())
                || (StringUtils.isBlank(userProfile.getLastName()) && StringUtils.isNotBlank(registerUser.getLastName()))
                || !userProfile.getLastName().equals(registerUser.getLastName())) {
            
            updateName(registerUser.getFirstName(), registerUser.getLastName(), registerUser.getQueueUserId());
            LOG.info("Updated name of user from={} to firstName={} lastName={} for qid={}",
                    userProfile.getName(),
                    registerUser.getFirstName(),
                    registerUser.getLastName(),
                    registerUser.getQueueUserId());
        }
    }

    /**
     * Prefer to call it in a thread as it takes a while to encrypt password.
     *
     * @param password
     * @param qid
     */
    private void createAuthentication(String password, String qid) {
        UserAuthenticationEntity userAuthentication = generateUserAuthentication(password);
        UserAccountEntity userAccount = findByQueueUserId(qid);
        userAccount.setUserAuthentication(userAuthentication);
        save(userAccount);
        LOG.info("Updated with authentication qid={}", qid);
    }

    public long countRegisteredBetweenDates(Date from, Date to) {
        return userAccountManager.countRegisteredBetweenDates(from, to);
    }
}
