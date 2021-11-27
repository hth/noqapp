package com.noqapp.service;

import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Constants;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.HashText;
import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.domain.ForgotRecoverEntity;
import com.noqapp.domain.GenerateUserIds;
import com.noqapp.domain.PointEarnedEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserAddressEntity;
import com.noqapp.domain.UserAuthenticationEntity;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.json.JsonUserAddress;
import com.noqapp.domain.types.AccountInactiveReasonEnum;
import com.noqapp.domain.types.CommunicationModeEnum;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.domain.types.PersonalityTraitsEnum;
import com.noqapp.domain.types.PointActivityEnum;
import com.noqapp.domain.types.RoleEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.repository.ForgotRecoverManager;
import com.noqapp.repository.PointEarnedManager;
import com.noqapp.repository.UserAccountManager;
import com.noqapp.repository.UserAuthenticationManager;
import com.noqapp.repository.UserPreferenceManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.exceptions.AccountEmailValidationException;
import com.noqapp.service.exceptions.DuplicateAccountException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private PointEarnedManager pointEarnedManager;
    private GenerateUserIdService generateUserIdService;
    private EmailValidateService emailValidateService;
    private ForgotRecoverManager forgotRecoverManager;
    private UserAddressService userAddressService;
    private StringRedisTemplate stringRedisTemplate;

    private ExecutorService executorService;

    @Autowired
    public AccountService(
        UserAccountManager userAccountManager,
        UserAuthenticationManager userAuthenticationManager,
        UserPreferenceManager userPreferenceManager,
        UserProfileManager userProfileManager,
        PointEarnedManager pointEarnedManager,
        GenerateUserIdService generateUserIdService,
        EmailValidateService emailValidateService,
        ForgotRecoverManager forgotRecoverManager,
        UserAddressService userAddressService,
        StringRedisTemplate stringRedisTemplate
    ) {
        this.userAccountManager = userAccountManager;
        this.userAuthenticationManager = userAuthenticationManager;
        this.userPreferenceManager = userPreferenceManager;
        this.userProfileManager = userProfileManager;
        this.pointEarnedManager = pointEarnedManager;
        this.generateUserIdService = generateUserIdService;
        this.emailValidateService = emailValidateService;
        this.forgotRecoverManager = forgotRecoverManager;
        this.userAddressService = userAddressService;
        this.stringRedisTemplate = stringRedisTemplate;

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

    /** Creates new user for client or business account. There are some rollback but this process should not fail. */
    public UserAccountEntity createNewAccount(
        String phone,
        String firstName,
        String lastName,
        String mail,
        String birthday,
        GenderEnum gender,
        String countryShortName,
        String timeZone,
        String password,
        String inviteCode,
        boolean phoneValidated,
        boolean dependent
    ) {
        String phoneWithCountryCode = Formatter.phoneCleanup(phone);
        String phoneRaw = Formatter.phoneStripCountryCode("+" + phoneWithCountryCode);
        String firstNameCleanedUp = WordUtils.capitalizeFully(StringUtils.normalizeSpace(firstName));
        String lastNameCleanedUp = WordUtils.capitalizeFully(StringUtils.normalizeSpace(lastName));
        LOG.debug("Check by phoneWithCountryCode={} phoneRaw={}", phoneWithCountryCode, phoneRaw);

        UserProfileEntity existingUserProfile = checkUserExistsByPhone(phoneWithCountryCode);
        if (dependent) {
            if (null == existingUserProfile) {
                LOG.error("Checked as dependent for phone={} but not found any one registered on it", phoneWithCountryCode);
                throw new RuntimeException("No one registered with this number.");
            }
            existingUserProfile = null;
        }

        if (null == existingUserProfile && null == doesUserExists(mail)) {
            UserAccountEntity userAccount = null;
            UserProfileEntity userProfile;

            String qid = getNextQID();
            while (userAccountManager.findByQueueUserId(qid) != null) {
                qid = getNextQID();
            }

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

                UserAuthenticationEntity userAuthentication = generateUserAuthentication(password);
                userAccount.setUserAuthentication(userAuthentication);
                userAccountManager.save(userAccount);

                if (StringUtils.isBlank(mail)) {
                    mail = RandomString.generateEmailAddressWithDomain(new ScrubbedInput(firstNameCleanedUp), new ScrubbedInput(lastNameCleanedUp), qid);
                    userAccount.setUserId(mail);
                    userAccountManager.save(userAccount);
                }
                LOG.info("Created UserAccount={} dob={}", userAccount, birthday);

                userProfile = UserProfileEntity.newInstance(
                    mail,
                    firstNameCleanedUp,
                    lastNameCleanedUp,
                    qid,
                    birthday
                );

                if (dependent) {
                    existingUserProfile = checkUserExistsByPhone(phoneWithCountryCode);
                    userProfile.setPhone(qid);
                    userProfile.setPhoneRaw(qid);
                    userProfile.setGuardianPhone(phoneWithCountryCode);
                    userProfile.setTimeZone(existingUserProfile.getTimeZone());
                } else {
                    userProfile.setPhone(phoneWithCountryCode);
                    userProfile.setPhoneRaw(phoneRaw);
                    userProfile.setTimeZone(timeZone);
                }
                userProfile.setGender(gender);
                userProfile.setCountryShortName(countryShortName);
                String generatedInviteCode = RandomString.generateInviteCode(firstNameCleanedUp, lastNameCleanedUp, qid);
                while (null != userProfileManager.inviteCodeExists(generatedInviteCode)) {
                    generatedInviteCode = RandomString.generateInviteCode(firstNameCleanedUp, lastNameCleanedUp, qid);
                }
                userProfile.setInviteCode(generatedInviteCode);
                userProfileManager.save(userProfile);

                if (dependent) {
                    UserProfileEntity guardianUserProfile = checkUserExistsByPhone(phoneWithCountryCode);
                    guardianUserProfile.addQidOfDependent(userProfile.getQueueUserId());
                    userProfileManager.save(guardianUserProfile);

                    LOG.info("Update guardian profile qid={} & dependent qid={}",
                            guardianUserProfile.getQueueUserId(), userProfile.getQueueUserId());
                }
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

            createPreferences(userProfile.getQueueUserId());
            if (StringUtils.isNotBlank(inviteCode)) {
                UserProfileEntity userProfileOfInvitee = findProfileByInviteCode(inviteCode);
                if (null != userProfileOfInvitee) {
                    pointEarnedManager.save(new PointEarnedEntity(qid, PointActivityEnum.INV));
                    pointEarnedManager.save(new PointEarnedEntity(userProfileOfInvitee.getQueueUserId(), PointActivityEnum.ISU));
                }
            }
            return userAccount;
        } else {
            LOG.error("Account creation failed as it already exists for phone={} mail={}", phoneWithCountryCode, mail);
            throw new DuplicateAccountException("Account with credential already exists");
        }
    }

    private String getNextQID() {
        List<String> missingQids = new ArrayList<>();

        try {
            if (stringRedisTemplate.hasKey(Constants.MISSING_QUEUE_IDS)) {
                String stringOfMissingQids = stringRedisTemplate.opsForValue().get(Constants.MISSING_QUEUE_IDS);
                String elements = stringOfMissingQids
                    .replaceAll("\\[", "")
                    .replaceAll("\\]", "")
                    .replaceAll(" ", "")
                    .trim();

                if (StringUtils.isNotBlank(elements) && elements.contains(",")) {
                    String[] a = elements.split(",");
                    missingQids = Arrays.asList(a);
                }

                if (missingQids.isEmpty()) {
                    boolean status = stringRedisTemplate.delete(Constants.MISSING_QUEUE_IDS);
                    LOG.info("Found empty {} removing the keys {}", Constants.MISSING_QUEUE_IDS, status);
                } else {
                    LOG.info("Found missing qids={} {}", missingQids.toArray(), elements);
                }
            }
        } catch (NullPointerException e) {
            LOG.error("Failed with NPE reading from redis {}", e.getLocalizedMessage(), e);
        } catch (Exception e) {
            LOG.error("Failed reading from redis {}", e.getLocalizedMessage(), e);
        }

        String qid;
        if (missingQids.isEmpty() || missingQids.contains(String.valueOf(GenerateUserIds.STARTING_USER_ID))) {
            qid = generateUserIdService.getNextAutoGeneratedUserId();
        } else {
            qid = missingQids.iterator().next();
            LOG.warn("Using missing qid={}", qid);
            List<String> filteredCollection = missingQids.stream().filter(e -> !e.equalsIgnoreCase(qid)).collect(Collectors.toList());
            stringRedisTemplate.opsForValue().set(Constants.MISSING_QUEUE_IDS, filteredCollection.toString());
        }
        return qid;
    }

    /**
     * Creates new agent for client or business account. There are some rollback but this process should not fail.
     * Defaults role to Q_SUPERVISOR
     */
    public UserAccountEntity createNewAgentAccount(
        String qidOfAdmin,
        String firstName,
        String lastName,
        String mail,
        String birthday,
        GenderEnum gender,
        String password
    ) {
        UserProfileEntity userProfileOfAdmin = userProfileManager.findByQueueUserId(qidOfAdmin);
        String firstNameCleanedUp = WordUtils.capitalizeFully(StringUtils.normalizeSpace(firstName));
        String lastNameCleanedUp = WordUtils.capitalizeFully(StringUtils.normalizeSpace(lastName));
        if (StringUtils.isNotBlank(mail) && null == doesUserExists(mail)) {
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
                userAccount.setAccountValidated(true);
                userAccount.setAccountValidatedBeginDate();
                userAccount.setPhoneValidated(false);
                userAccount.active();

                UserAuthenticationEntity userAuthentication = generateUserAuthentication(password);
                userAccount.setUserAuthentication(userAuthentication);
                userAccount.setRoles(new HashSet<>() {{add(RoleEnum.ROLE_Q_SUPERVISOR);}});
                userAccountManager.save(userAccount);
                LOG.info("Created UserAccount={}", userAccount);

                userProfile = UserProfileEntity.newInstance(
                    mail,
                    firstNameCleanedUp,
                    lastNameCleanedUp,
                    qid,
                    birthday
                );

                userProfile.setPhone(qid);
                userProfile.setPhoneRaw(qid);
                userProfile.setTimeZone(userProfileOfAdmin.getTimeZone());
                userProfile.setGender(gender);
                userProfile.setCountryShortName(userProfileOfAdmin.getCountryShortName());
                String generatedInviteCode = RandomString.generateInviteCode(firstNameCleanedUp, lastNameCleanedUp, qid);
                while (null != userProfileManager.inviteCodeExists(generatedInviteCode)) {
                    generatedInviteCode = RandomString.generateInviteCode(firstNameCleanedUp, lastNameCleanedUp, qid);
                }
                userProfile.setInviteCode(generatedInviteCode);
                userProfile.setLevel(UserLevelEnum.Q_SUPERVISOR);
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

            createPreferences(userProfile.getQueueUserId());
            return userAccount;
        } else {
            LOG.error("Account creation failed as it already exists for mail={}", mail);
            throw new DuplicateAccountException("Account with credential already exists");
        }
    }

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
    private void createPreferences(String qid) {
        try {
            UserPreferenceEntity userPreferenceEntity = UserPreferenceEntity.newInstance(qid)
                .setPromotionalSMS(CommunicationModeEnum.R)
                .setFirebaseNotification(CommunicationModeEnum.R);
            userPreferenceManager.save(userPreferenceEntity);
            LOG.debug("Created UserPreferenceEntity={}", userPreferenceEntity.getQueueUserId());
        } catch (Exception e) {
            LOG.error("Saving UserPreferenceEntity qid={} reason={}", qid, e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving user preference");
        }
    }

    public List<UserProfileEntity> findDependentProfiles(String qid) {
        UserProfileEntity userProfile = findProfileByQueueUserId(qid);
        return userProfileManager.findDependentProfilesByPhone(userProfile.getPhone());
    }

    Set<String> findDependentQIDByPhone(String qid) {
        UserProfileEntity userProfile = findProfileByQueueUserId(qid);
        return userProfileManager.findDependentQIDByPhone(userProfile.getPhone());
    }

    @Mobile
    public boolean reachedMaxDependents(String qid) {
        UserProfileEntity userProfile = findProfileByQueueUserId(qid);
        long count = userProfileManager.countDependentProfilesByPhone(userProfile.getPhone());
        return 5 <= count;
    }

    /**
     * Should be called when from catch condition of DataIntegrityViolationException.
     */
    public void deleteAllWhenAccountCreationFailedDueToDuplicate(UserAccountEntity userAccount, DataIntegrityViolationException e) {
        Assert.notNull(e, "DataIntegrityViolationException is not set or not invoked properly");

        userAuthenticationManager.deleteHard(userAccount.getUserAuthentication());
        userAccountManager.deleteHard(userAccount);

        UserPreferenceEntity userPreference = userPreferenceManager.findByQueueUserId(userAccount.getQueueUserId());
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

        String firstNameCleanedUp = WordUtils.capitalizeFully(StringUtils.normalizeSpace(firstName));
        String lastNameCleanedUp = WordUtils.capitalizeFully(StringUtils.normalizeSpace(lastName));

        /* For display name we set the first name and last name. */
        userAccount.setFirstName(firstNameCleanedUp);
        userAccount.setLastName(lastNameCleanedUp);
        userAccount.setDisplayName(userAccount.getName());

        /* Changed to use a query as it fixes versioning issue. */
        userAccountManager.updateName(firstNameCleanedUp, lastNameCleanedUp, userAccount.getName(), qid);
        userProfileManager.updateName(firstNameCleanedUp, lastNameCleanedUp, qid);
    }

    public void validateAccount(EmailValidateEntity emailValidate, String qid) {
        markAccountValidated(qid);

        emailValidate.inActive();
        emailValidateService.saveEmailValidateEntity(emailValidate);
    }

    @Mobile
    public UserAccountEntity validateAccount(String qid, String mailOTP) {
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
        if (StringUtils.isNotBlank(userProfile.getMailOTP()) && userProfile.getMailOTP().equals(mailOTP)) {
            LOG.info("Validated email of {}", qid);
            return userAccountManager.markAccountAsValid(qid);
        }

        throw new AccountEmailValidationException("Failed validating account");
    }

    private void markAccountValidated(String qid) {
        UserAccountEntity userAccount = findByQueueUserId(qid);
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
            userAccountManager.markAccountAsValid(qid);
        }
    }

    private void updateAccountToValidated(String id, AccountInactiveReasonEnum accountInactiveReason) {
        userAccountManager.updateAccountToValidated(id, accountInactiveReason);
    }

    /** Change profile user level. */
    public void changeUserLevel(String qid, UserLevelEnum userLevel) {
        userProfileManager.changeUserLevel(qid, userLevel);
    }

    /** Change user role to match user level. */
    public UserAccountEntity changeAccountRolesToMatchUserLevel(String qid, UserLevelEnum userLevel) {
        UserAccountEntity userAccount = findByQueueUserId(qid);
        Set<RoleEnum> roles = new LinkedHashSet<>();
        switch (userLevel) {
            case TECHNICIAN:
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                userAccount.setRoles(roles);
                break;
            case MEDICAL_TECHNICIAN:
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_MEDICAL_TECHNICIAN);
                userAccount.setRoles(roles);
                break;
            case SUPERVISOR:
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_TECHNICIAN);
                roles.add(RoleEnum.ROLE_MEDICAL_TECHNICIAN);
                roles.add(RoleEnum.ROLE_SUPERVISOR);
                userAccount.setRoles(roles);
                break;
            case ADMIN:
                /* As of now admin does not have any Business role. */
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
                if (!userAccount.isPhoneValidated()) {
                    roles.add(RoleEnum.ROLE_Q_SUPERVISOR);
                } else {
                    roles.add(RoleEnum.ROLE_CLIENT);
                    roles.add(RoleEnum.ROLE_Q_SUPERVISOR);
                }
                userAccount.setRoles(roles);
                break;
            case A_SUPERVISOR:
                if (!userAccount.isPhoneValidated()) {
                    roles.add(RoleEnum.ROLE_MAS);
                } else {
                    roles.add(RoleEnum.ROLE_CLIENT);
                    roles.add(RoleEnum.ROLE_MAS);
                }
                userAccount.setRoles(roles);
                break;
            case S_MANAGER:
                if (!userAccount.isPhoneValidated()) {
                    roles.add(RoleEnum.ROLE_S_MANAGER);
                } else {
                    roles.add(RoleEnum.ROLE_CLIENT);
                    roles.add(RoleEnum.ROLE_Q_SUPERVISOR);
                    roles.add(RoleEnum.ROLE_S_MANAGER);
                }
                userAccount.setRoles(roles);
                break;
            case M_ACCOUNTANT:
                if (!userAccount.isPhoneValidated()) {
                    roles.add(RoleEnum.ROLE_M_ACCOUNTANT);
                } else {
                    roles.add(RoleEnum.ROLE_CLIENT);
                    roles.add(RoleEnum.ROLE_M_ACCOUNTANT);
                }
                userAccount.setRoles(roles);
                break;
            case M_ADMIN:
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_Q_SUPERVISOR);
                roles.add(RoleEnum.ROLE_S_MANAGER);
                roles.add(RoleEnum.ROLE_M_ACCOUNTANT);
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
    public UserAccountEntity updateUID(String existingUserId, String newUserId) {
        if (null != findByUserId(newUserId)) {
            LOG.info("Account already exists with email {} {}", newUserId, existingUserId);
            throw new DuplicateAccountException("Account already exists " + newUserId);
        }

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
        LOG.info("Update profile with registeredUser={} username={}", registerUser, username);
        UserProfileEntity userProfile;
        if (!registerUser.getEmail().equalsIgnoreCase(username)) {
            updateUID(username, registerUser.getEmail());
            userProfile = doesUserExists(registerUser.getEmail());
            registerUser.setEmailValidated(false);
        } else {
            userProfile = doesUserExists(username);
        }

        userProfile.setGender(registerUser.getGender());
        if (StringUtils.isNotBlank(registerUser.getAddress())) {
            JsonUserAddress jsonUserAddress = registerUser.getJsonUserAddress();

            if (null != jsonUserAddress && StringUtils.isNotBlank(jsonUserAddress.getAddress())) {
                /* Note: Address set in the JsonUserAddress has higher preference. Always use that address. */
                UserAddressEntity userAddress = userAddressService.findByAddress(registerUser.getQueueUserId(), jsonUserAddress.getAddress());
                if (null == userAddress) {
                    userAddressService.saveAddress(
                        CommonUtil.generateHexFromObjectId(),
                        registerUser.getQueueUserId(),
                        jsonUserAddress
                    );
                } else {
                    LOG.info("Address already exists for qid={} address=\"{}\"", registerUser.getQueueUserId(), jsonUserAddress.getAddress());
                }
            } else {
                /* Should not reach this condition. */
                LOG.warn("No address supplied condition reached for JsonUSerAddress qid={}", registerUser.getQueueUserId());
            }
        }
        userProfile.setCountryShortName(registerUser.getCountryShortName());
        if (StringUtils.isBlank(userProfile.getGuardianPhone())) {
            LOG.debug("Raw={}, WithCountry={}", registerUser.getPhoneNotFormatted(), registerUser.getPhoneWithCountryCode());
            /* Fix to make sure county code is not appended to Phone Raw. */
            userProfile.setPhone(registerUser.getPhoneWithCountryCode());
            userProfile.setPhoneRaw(registerUser.getPhoneNotFormatted());
        }
        userProfile.setTimeZone(registerUser.getTimeZone());
        userProfile.setBirthday(registerUser.getBirthday());
        save(userProfile);

        if (!userProfile.getName().equalsIgnoreCase(registerUser.getName())) {
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

    public String updateAuthenticationKey(String id) {
        String updatedAuthenticationKey = HashText.computeBCrypt(RandomString.newInstance().nextString());
        userAuthenticationManager.updateAuthenticationKey(id, updatedAuthenticationKey);
        return updatedAuthenticationKey;
    }

    public void updatePersonalityTrait(String qid, PersonalityTraitsEnum personalityTraits) {
        if (null != personalityTraits && Validate.isValidQid(qid)) {
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
            userProfile.setPersonalityTraits(personalityTraits);
            userProfileManager.save(userProfile);
        }
    }

    public String updatePhoneNumber(String qid, String newPhone, String countryShortName, String timeZone) {
        RegisterUser registerUser = new RegisterUser();
        registerUser.setPhone(new ScrubbedInput(newPhone))
            .setCountryShortName(new ScrubbedInput(countryShortName))
            .setTimeZone(new ScrubbedInput(timeZone));

        UserProfileEntity userProfile  = userProfileManager.findByQueueUserId(qid);
        String oldPhone = userProfile.getPhone();
        userProfile.setPhone(registerUser.getPhoneWithCountryCode());
        userProfile.setPhoneRaw(registerUser.getPhoneNotFormatted());
        userProfile.setCountryShortName(registerUser.getCountryShortName());
        userProfile.setTimeZone(registerUser.getTimeZone());

        save(userProfile);
        executorService.submit(() -> updateDependentsPhoneNumber(
            registerUser.getPhoneWithCountryCode(),
            registerUser.getCountryShortName(),
            registerUser.getTimeZone(),
            oldPhone));
        UserAccountEntity userAccount = findByQueueUserId(qid);
        return updateAuthenticationKey(userAccount.getUserAuthentication().getId());
    }

    private void updateDependentsPhoneNumber(String newPhone, String countryShortName, String timeZone, String oldPhone) {
        List<UserProfileEntity> dependentUserProfiles = userProfileManager.findDependentProfilesByPhone(oldPhone);
        for (UserProfileEntity dependentUserProfile : dependentUserProfiles) {
            boolean status = userProfileManager.updateDependentDetailsOnPhoneMigration(
                dependentUserProfile.getQueueUserId(),
                newPhone,
                countryShortName,
                timeZone
            );
            LOG.info("Guardian phone updated status={} for qid={}", status, dependentUserProfile.getQueueUserId());
        }
    }

    void addUserProfileImage(String qid, String profileImage) {
        userProfileManager.addUserProfileImage(qid, profileImage);
    }

    void unsetUserProfileImage(String qid) {
        userProfileManager.unsetUserProfileImage(qid);
    }

    @Mobile
    public void unsetMailOTP(String id) {
        userProfileManager.unsetMailOTP(id);
    }

    public boolean isPhoneValidated(String qid) {
        return userAccountManager.isPhoneValidated(qid);
    }

    public void increaseOTPCount(String qid) {
        userAccountManager.increaseOTPCount(qid);
    }

    public void resetOTPCount(String qid) {
        userAccountManager.resetOTPCount(qid);
    }

    public boolean accountOpenedInLast10Days(String qid) {
        UserAccountEntity userAccount = userAccountManager.findByQueueUserId(qid);
        if (!userAccount.isAccountValidated()) {
            return false;
        }

        return DateUtil.getDaysBetween(userAccount.getCreated()) >= 10;
    }

    public Stream<UserAccountEntity> getAccountsWithLimitedAccess(AccountInactiveReasonEnum accountInactiveReason) {
        return userAccountManager.getAccountsWithLimitedAccess(accountInactiveReason);
    }

    @Mobile
    public UserPreferenceEntity getEarnedPoint(String qid) {
        return userPreferenceManager.getEarnedPoint(qid);
    }
}
