package com.noqapp.loader.service;

import com.noqapp.domain.jms.ChangeMailOTP;
import com.noqapp.domain.jms.SignupUserInfo;
import com.noqapp.service.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 7/20/20 6:44 PM
 */
@Service
public class JMSConsumerService {
    private static final Logger LOG = LoggerFactory.getLogger(JMSConsumerService.class);

    private MailService mailService;

    @Autowired
    public JMSConsumerService(MailService mailService) {
        this.mailService = mailService;
    }

    @JmsListener(destination = "${activemq.destination.mail.signup}", containerFactory = "jmsMailSingUpListenerContainerFactory")
    public void sendMailOnSignUp(SignupUserInfo signupUserInfo) {
        LOG.info("ActiveMQ received {}", signupUserInfo);
        mailService.sendValidationMailOnAccountCreation(signupUserInfo.getUserId(), signupUserInfo.getQid(), signupUserInfo.getName());
    }

    @JmsListener(destination = "${activemq.destination.mail.change}", containerFactory = "jmsMailChangeListenerContainerFactory")
    public void sendMailOnChangeInMail(ChangeMailOTP changeMailOTP) {
        LOG.info("ActiveMQ received {}", changeMailOTP);
        mailService.sendOTPMail(changeMailOTP.getUserId(), changeMailOTP.getName(), changeMailOTP.getMailOTP(), "email address");
    }
}
