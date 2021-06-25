package com.noqapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.domain.UserAccountEntity;

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
        assertEquals(false, userAccount1.isAccountValidated(), "Account is not validated");

        EmailValidateEntity emailValidate1 = emailValidateService.saveAccountValidate(userAccount1.getQueueUserId(), userAccount1.getUserId());
        accountService.validateAccount(emailValidate1, userAccount1.getQueueUserId());
        UserAccountEntity userAccount1_after_validation = accountService.findByQueueUserId(userAccount1.getQueueUserId());
        assertEquals(true, userAccount1_after_validation.isAccountValidated(), "Account is validated");
    }
}
