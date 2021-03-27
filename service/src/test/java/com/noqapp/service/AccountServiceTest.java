package com.noqapp.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.repository.ForgotRecoverManager;
import com.noqapp.repository.UserAccountManager;
import com.noqapp.repository.UserAuthenticationManager;
import com.noqapp.repository.UserPreferenceManager;
import com.noqapp.repository.UserProfileManager;

import org.springframework.dao.DataIntegrityViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * User: hitender
 * Date: 11/20/16 5:48 PM
 */
class AccountServiceTest {
    @Mock private UserAccountManager userAccountManager;
    @Mock private UserAuthenticationManager userAuthenticationManager;
    @Mock private UserPreferenceManager userPreferenceManager;
    @Mock private UserProfileManager userProfileManager;
    @Mock private GenerateUserIdService generateUserIdService;
    @Mock private EmailValidateService emailValidateService;
    @Mock private InviteService inviteService;
    @Mock private ForgotRecoverManager forgotRecoverManager;
    @Mock private UserAddressService userAddressService;

    private AccountService accountService;

    @Mock UserAccountEntity userAccount;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountService(
            5,
            userAccountManager,
            userAuthenticationManager,
            userPreferenceManager,
            userProfileManager,
            generateUserIdService,
            emailValidateService,
            inviteService,
            forgotRecoverManager,
            userAddressService
        );
    }

    @Test
    @DisplayName ("Find When User Does Not Exists")
    void testFindIfUser_Does_Not_Exists() {
        when(userProfileManager.findOneByMail(anyString())).thenReturn(null);
        assertNull(accountService.findByQueueUserId("user_community_3@noqapp.com"));
    }

    @Test
    @DisplayName("Throw data integrity violation exception when version number is different")
    void failed_to_save_userAccount_with_dataIntegrityViolationException() {
        when(userAccount.getQueueUserId()).thenReturn("test_id");
        doThrow(DataIntegrityViolationException.class).when(userAccountManager).save(userAccount);
        Throwable exception = assertThrows(DataIntegrityViolationException.class,
                ()-> accountService.save(userAccount));
        assertNull(exception.getMessage(), "No message is set when data integrity violation happens");
    }
}
