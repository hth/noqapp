package com.token.service;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.token.repository.UserAccountManager;
import com.token.repository.UserAuthenticationManager;
import com.token.repository.UserPreferenceManager;
import com.token.repository.UserProfileManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * User: hitender
 * Date: 11/20/16 5:48 PM
 */
public class AccountServiceTest {
    @Mock private UserAccountManager userAccountManager;
    @Mock private UserAuthenticationManager userAuthenticationManager;
    @Mock private UserPreferenceManager userPreferenceManager;
    @Mock private UserProfileManager userProfileManager;
    @Mock private GenerateUserIdService generateUserIdService;
    @Mock private NotificationService notificationService;

    private AccountService accountService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        accountService = new AccountService(
                userAccountManager,
                userAuthenticationManager,
                userPreferenceManager,
                userProfileManager,
                generateUserIdService,
                notificationService);
    }

    @Test
    public void testFindIfUser_Does_Not_Exists() throws Exception {
        when(userProfileManager.findOneByMail(anyString())).thenReturn(null);
        assertNull(accountService.findByReceiptUserId("user_community_3@receiptofi.com"));
    }
}