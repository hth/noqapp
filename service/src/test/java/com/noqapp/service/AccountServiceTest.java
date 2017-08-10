package com.noqapp.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.noqapp.repository.ForgotRecoverManager;
import com.noqapp.repository.UserAccountManager;
import com.noqapp.repository.UserAuthenticationManager;
import com.noqapp.repository.UserPreferenceManager;
import com.noqapp.repository.UserProfileManager;
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

    private AccountService accountService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        accountService = new AccountService(
                userAccountManager,
                userAuthenticationManager,
                userPreferenceManager,
                userProfileManager,
                generateUserIdService,
                emailValidateService,
                inviteService,
                forgotRecoverManager);
    }

    @Test
    @DisplayName ("Find When User Does Not Exists")
    void testFindIfUser_Does_Not_Exists() throws Exception {
        when(userProfileManager.findOneByMail(anyString())).thenReturn(null);
        assertNull(accountService.findByQueueUserId("user_community_3@noqapp.com"));
    }
}