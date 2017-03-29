package com.token.service;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.token.domain.EmailValidateEntity;
import com.token.domain.UserAccountEntity;
import com.token.domain.UserAuthenticationEntity;
import com.token.domain.UserPreferenceEntity;
import com.token.domain.UserProfileEntity;
import com.token.domain.annotation.Mobile;
import com.token.domain.types.AccountInactiveReasonEnum;
import com.token.domain.types.NotificationGroupEnum;
import com.token.domain.types.NotificationTypeEnum;
import com.token.domain.types.ProviderEnum;
import com.token.domain.types.RoleEnum;
import com.token.domain.types.UserLevelEnum;
import com.token.repository.UserAccountManager;
import com.token.repository.UserAuthenticationManager;
import com.token.repository.UserPreferenceManager;
import com.token.repository.UserProfileManager;
import com.token.utils.Formatter;
import com.token.utils.HashText;
import com.token.utils.RandomString;
import com.token.utils.ScrubbedInput;

import java.util.LinkedHashSet;
import java.util.Set;

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
    private NotificationService notificationService;
    private EmailValidateService emailValidateService;

    @Autowired
    public AccountService(
            UserAccountManager userAccountManager,
            UserAuthenticationManager userAuthenticationManager,
            UserPreferenceManager userPreferenceManager,
            UserProfileManager userProfileManager,
            GenerateUserIdService generateUserIdService,
            NotificationService notificationService,
            EmailValidateService emailValidateService) {
        this.userAccountManager = userAccountManager;
        this.userAuthenticationManager = userAuthenticationManager;
        this.userPreferenceManager = userPreferenceManager;
        this.userProfileManager = userProfileManager;
        this.generateUserIdService = generateUserIdService;
        this.notificationService = notificationService;
        this.emailValidateService = emailValidateService;
    }

    public UserProfileEntity doesUserExists(String mail) {
        return userProfileManager.findOneByMail(mail);
    }
    
    public UserProfileEntity checkUserExistsByPhone(String phone, String countryShortName) {
        return userProfileManager.findOneByPhone(Formatter.phoneNumberWithCountryCode(Formatter.phoneCleanup(phone), countryShortName));
    }

    public UserAccountEntity findByReceiptUserId(String rid) {
        return userAccountManager.findByReceiptUserId(rid);
    }

    @Mobile
    public UserAccountEntity findByUserId(String mail) {
        return userAccountManager.findByUserId(mail);
    }

    public UserAccountEntity findByProviderUserId(String providerUserId) {
        return userAccountManager.findByProviderUserId(providerUserId);
    }

    public UserAccountEntity findByAuthorizationCode(ProviderEnum provider, String authorizationCode) {
        return userAccountManager.findByAuthorizationCode(provider, authorizationCode);
    }

    public void save(UserAccountEntity userAccount) {
        try {
            userAccountManager.save(userAccount);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserAccountEntity={}", e.getLocalizedMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error("Saving UserAccount rid={} reason={}", userAccount.getReceiptUserId(), e.getLocalizedMessage(), e);
            throw new RuntimeException("Error saving user account");
        }
    }

    public void saveUserAccount(UserAccountEntity userAccount) {
        userAccountManager.save(userAccount);
    }

    /**
     * Creates new user for client or merchant account. There are some rollback but this process should not fail.
     *
     * @param mail
     * @param firstName
     * @param lastName
     * @param password
     * @param birthday
     * @return
     */
    @Mobile
    public UserAccountEntity createNewAccount(
            String phone,
            String firstName,
            String lastName,
            String mail,
            String birthday,
            String gender,
            String countryShortName,
            String timeZone,
            String password
    ) {
        String phoneRaw = Formatter.phoneCleanup(phone);
        String phoneWithCountryCode = Formatter.phoneNumberWithCountryCode(phoneRaw, countryShortName);
        if (userProfileManager.findOneByPhone(phoneWithCountryCode) == null) {
            UserAccountEntity userAccount = null;
            UserProfileEntity userProfile;

            UserAuthenticationEntity userAuthentication = generateUserAuthentication(password);
            String rid = generateUserIdService.getNextAutoGeneratedUserId();
            try {

                userAccount = UserAccountEntity.newInstance(
                        rid,
                        mail,
                        firstName,
                        lastName,
                        userAuthentication
                );
                userAccount.setAccountValidated(false);
                userAccount.setAccountValidatedBeginDate();
                userAccount.active();
                userAccountManager.save(userAccount);

                if (StringUtils.isBlank(mail)) {
                    mail = RandomString.generateEmailAddressWithDomain(new ScrubbedInput(firstName), new ScrubbedInput(lastName), rid);
                    userAccount.setUserId(mail);
                    userAccountManager.save(userAccount);
                }

                userProfile = UserProfileEntity.newInstance(
                        mail,
                        firstName,
                        lastName,
                        rid,
                        birthday
                );
                userProfile.setPhone(phoneWithCountryCode);
                userProfile.setPhoneRaw(phoneRaw);
                userProfile.setGender(gender);
                userProfile.setCountryShortName(countryShortName);
                userProfile.setTimeZone(timeZone);
                userProfileManager.save(userProfile);
            } catch (Exception e) {
                LOG.error("During saving UserProfileEntity={}", e.getLocalizedMessage(), e);

                //Roll back
                if (userAccount != null) {
                    userAccountManager.deleteHard(userAccount);
                }
                userAuthenticationManager.deleteHard(userAuthentication);
                throw new RuntimeException("Error saving user profile");
            }

            createPreferences(userProfile);
            addWelcomeNotification(userAccount);
            return userAccount;
        } else {
            LOG.error("Account creation failed as it already exists for phone={}", phoneWithCountryCode);
            return null;
        }
    }

    /**
     * Create new account using social login.
     */
    public void createNewMerchantAccount(UserAccountEntity userAccount) {
        Assert.notNull(userAccount.getProviderId(), "Missing provider id for social login");
        LOG.info("New account created using social user={} provider={}",
                userAccount.getReceiptUserId(), userAccount.getProviderId());
        /**
         * UserAuthenticationEntity is not required but needed. Social user will not be able to reset the authentication
         * since its a social account.
         */
        UserAuthenticationEntity userAuthentication = getUserAuthenticationEntity();
        userAccount.setUserAuthentication(userAuthentication);
        save(userAccount);
    }

    /**
     * Save userProfile.
     */
    public void save(UserProfileEntity userProfile) {
        try {
            userProfileManager.save(userProfile);
            LOG.debug("Created UserProfileEntity={} id={}", userProfile.getReceiptUserId(), userProfile.getId());
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserProfileEntity={}", e.getLocalizedMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error("Saving UserProfile rid={} reason={}", userProfile.getReceiptUserId(), e.getLocalizedMessage(), e);
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
                HashText.computeBCrypt(password),
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
        String code = HashText.computeBCrypt(RandomString.newInstance().nextString());
        UserAuthenticationEntity userAuthentication = UserAuthenticationEntity.newInstance(
                code,
                code
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
            LOG.debug("Created UserPreferenceEntity={}", userPreferenceEntity.getReceiptUserId());
        } catch (Exception e) {
            LOG.error("Saving UserPreferenceEntity rid={} reason={}", userProfile.getReceiptUserId(), e.getLocalizedMessage(), e);
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

        UserPreferenceEntity userPreference = userPreferenceManager.getByRid(userAccount.getReceiptUserId());
        if (null != userPreference) {
            userPreferenceManager.deleteHard(userPreference);
        }

        UserProfileEntity userProfile = userProfileManager.findByReceiptUserId(userAccount.getReceiptUserId());
        if (null != userProfile) {
            userProfileManager.deleteHard(userProfile);
        }
    }

    private void addWelcomeNotification(UserAccountEntity userAccount) {
        notificationService.addNotification(
                "Welcome " + userAccount.getName() + ". Next step, take a picture of your receipt from app to process it.",
                NotificationTypeEnum.PUSH_NOTIFICATION,
                NotificationGroupEnum.N,
                userAccount.getReceiptUserId());
    }

    public UserProfileEntity findProfileByReceiptUserId(String receiptUserId) {
        return userProfileManager.findByReceiptUserId(receiptUserId);
    }

    public void updateName(String firstName, String lastName, String rid) {
        UserAccountEntity userAccount = findByReceiptUserId(rid);
        UserProfileEntity userProfile = userProfileManager.findByReceiptUserId(rid);

        userAccount.setFirstName(firstName);
        userAccount.setLastName(lastName);

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
                    LOG.error("Reached unreachable condition, rid={}", userAccount.getReceiptUserId());
                    throw new RuntimeException("Reached unreachable condition " + userAccount.getReceiptUserId());
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
     * @param rid
     * @param userLevel
     * @return
     */
    public UserAccountEntity changeAccountRolesToMatchUserLevel(String rid, UserLevelEnum userLevel) {
        UserAccountEntity userAccount = findByReceiptUserId(rid);
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
                /** As of now admin does not have any Merchant role. */
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
            case MER_MANAGER:
                roles.add(RoleEnum.ROLE_CLIENT);
                roles.add(RoleEnum.ROLE_MER_MANAGER);
                userAccount.setRoles(roles);
                break;
            case MER_ADMIN:
                roles.add(RoleEnum.ROLE_MER_ADMIN);
                roles.add(RoleEnum.ROLE_MER_MANAGER);
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
     * @see com.token.service.MailService#accountValidationMail(String, String, String) ()
     */
    @Mobile
    @SuppressWarnings ("unused")
    public UserAccountEntity updateUID(String existingUserId, String newUserId) {
        UserAccountEntity userAccount = findByUserId(existingUserId);
        if (!userAccount.isAccountValidated()) {
            emailValidateService.invalidateAllEntries(userAccount.getReceiptUserId());
        }
        userAccount.setUserId(newUserId);
        userAccount.setAccountValidated(false);
        userAccount.active();

        UserProfileEntity userProfile = doesUserExists(existingUserId);
        userProfile.setEmail(newUserId);

        /** Always update userAccount before userProfile */
        userAccountManager.save(userAccount);
        userProfileManager.save(userProfile);

        return userAccount;
    }
}
