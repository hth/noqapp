package com.token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.EmailValidateEntity;
import com.token.repository.EmailValidateManager;
import com.token.utils.HashText;
import com.token.utils.RandomString;

/**
 * User: hitender
 * Date: 11/25/16 10:03 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class EmailValidateService {

    private EmailValidateManager emailValidateManager;

    @Autowired
    public EmailValidateService(EmailValidateManager emailValidateManager) {
        this.emailValidateManager = emailValidateManager;
    }

    public EmailValidateEntity saveAccountValidate(String receiptUserId, String userId) {
        String authenticationKey = HashText.computeBCrypt(RandomString.newInstance().nextString());
        EmailValidateEntity emailValidate = EmailValidateEntity.newInstance(receiptUserId, userId, authenticationKey);
        saveEmailValidateEntity(emailValidate);
        return emailValidate;
    }

    void saveEmailValidateEntity(EmailValidateEntity emailValidate) {
        emailValidateManager.save(emailValidate);
    }

    public EmailValidateEntity findByAuthenticationKey(String key) {
        return emailValidateManager.findByAuthenticationKey(key);
    }

    void invalidateAllEntries(String receiptUserId) {
        emailValidateManager.invalidateAllEntries(receiptUserId);
    }
}
