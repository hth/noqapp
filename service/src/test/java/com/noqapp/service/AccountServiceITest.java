package com.noqapp.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.service.exceptions.DuplicateAccountException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * hitender
 * 2/20/21 2:07 PM
 */
@DisplayName("Account Service API")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("api")
class AccountServiceITest extends ITest {

    @Test
    void validateAccount() {
        UserAccountEntity userAccount1 = accountService.findByUserId("rocketd@r.com");
        assertFalse(userAccount1.isAccountValidated(), "Account is not validated");

        EmailValidateEntity emailValidate1 = emailValidateService.saveAccountValidate(userAccount1.getQueueUserId(), userAccount1.getUserId());
        accountService.validateAccount(emailValidate1, userAccount1.getQueueUserId());
        UserAccountEntity userAccount1_after_validation = accountService.findByQueueUserId(userAccount1.getQueueUserId());
        assertTrue(userAccount1_after_validation.isAccountValidated(), "Account is validated");
    }

    @Test
    void updateUID() {
        Exception exception = assertThrows(DuplicateAccountException.class, () -> {
            accountService.updateUID("pintod@r.com", "rocketd@r.com");
        });

        String expectedMessage = "Account already exists " + "rocketd@r.com";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
