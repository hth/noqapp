package com.noqapp.loader.service;

import com.noqapp.domain.jms.ChangeMailOTP;
import com.noqapp.domain.jms.FeedbackMail;
import com.noqapp.domain.jms.ReviewSentiment;
import com.noqapp.domain.jms.SignupUserInfo;
import com.noqapp.service.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
        LOG.info("ActiveMQ received on signup {}", signupUserInfo);
        mailService.sendValidationMailOnAccountCreation(signupUserInfo.getUserId(), signupUserInfo.getQid(), signupUserInfo.getName());
    }

    @JmsListener(destination = "${activemq.destination.mail.change}", containerFactory = "jmsMailChangeListenerContainerFactory")
    public void sendMailOnChangeInMail(ChangeMailOTP changeMailOTP) {
        LOG.info("ActiveMQ received on change mail {}", changeMailOTP);
        mailService.sendOTPMail(changeMailOTP.getUserId(), changeMailOTP.getName(), changeMailOTP.getMailOTP(), "email address");
    }

    @JmsListener(destination = "${activemq.destination.feedback}", containerFactory = "jmsFeedbackListenerContainerFactory")
    public void sendMailOnCFeedback(FeedbackMail feedbackMail) {
        LOG.info("ActiveMQ received on user feedback {}", feedbackMail);

        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("userId", feedbackMail.getUserId());
        rootMap.put("qid", feedbackMail.getQid());
        rootMap.put("name", feedbackMail.getName());
        rootMap.put("subject", feedbackMail.getSubject());
        rootMap.put("body", feedbackMail.getBody());
        mailService.sendAnyMail(
            "contact@noqapp.com",
            "NoQueue",
            "Feedback from: " + feedbackMail.getName(),
            rootMap,
            "mail/feedback.ftl");
    }

    @JmsListener(destination = "${activemq.destination.review.negative}", containerFactory = "jmsReviewNegativeListenerContainerFactory")
    public void sendMailOnReviewSentiment(ReviewSentiment reviewSentiment) {
        LOG.info("ActiveMQ received sentiments {}", reviewSentiment);

        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("storeName", reviewSentiment.getStoreName());
        rootMap.put("reviewerName", reviewSentiment.getReviewerName());
        rootMap.put("reviewerPhone", reviewSentiment.getReviewerPhone());
        rootMap.put("ratingCount", reviewSentiment.getRatingCount());
        rootMap.put("hourSaved", reviewSentiment.getHourSaved());
        rootMap.put("review", reviewSentiment.getReview());
        rootMap.put("sentiment", reviewSentiment.getSentiment());
        mailService.sendAnyMail(
            reviewSentiment.getSentimentWatcherEmail(),
            "Customer Sentiment Watcher",
            "Review for: " + reviewSentiment.getStoreName(),
            rootMap,
            "mail/reviewSentiment.ftl");
    }
}
