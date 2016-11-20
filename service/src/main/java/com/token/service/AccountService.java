package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.token.domain.UserAccountEntity;
import com.token.domain.UserAuthenticationEntity;
import com.token.domain.UserPreferenceEntity;
import com.token.domain.UserProfileEntity;
import com.token.domain.annotation.Mobile;
import com.token.domain.types.ProviderEnum;
import com.token.repository.UserAccountManager;
import com.token.repository.UserAuthenticationManager;
import com.token.repository.UserPreferenceManager;
import com.token.repository.UserProfileManager;
import com.token.utils.HashText;
import com.token.utils.RandomString;

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

    @Autowired
    public AccountService(
            UserAccountManager userAccountManager,
            UserAuthenticationManager userAuthenticationManager,
            UserPreferenceManager userPreferenceManager,
            UserProfileManager userProfileManager
    ) {
        this.userAccountManager = userAccountManager;
        this.userAuthenticationManager = userAuthenticationManager;
        this.userPreferenceManager = userPreferenceManager;
        this.userProfileManager = userProfileManager;
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
     * Create new account using social login.
     */
    public void createNewAccount(UserAccountEntity userAccount) {
        Assert.notNull(userAccount.getProviderId());
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

    public UserProfileEntity findProfileByReceiptUserId(String receiptUserId) {
        return userProfileManager.findByReceiptUserId(receiptUserId);
    }
}
