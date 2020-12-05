package com.noqapp.loader.scheduledtasks;

import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.MessageCustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * hitender
 * 11/30/20 6:57 PM
 */
@Component
public class MessageAllUser {
    private static final Logger LOG = LoggerFactory.getLogger(MessageAllUser.class);

    private String information;
    private String sendWeeklyInformation;

    public QueueManagerJDBC queueManagerJDBC;
    public MessageCustomerService messageCustomerService;
    public UserProfileManager userProfileManager;

    @Autowired
    public MessageAllUser(
        @Value("{subscribe.information}")
        String information,

        @Value("${makePreferredBusinessFiles:OFF}")
        String sendWeeklyInformation,

        QueueManagerJDBC queueManagerJDBC,
        MessageCustomerService messageCustomerService,
        UserProfileManager userProfileManager
    ) {
        this.information = information;
        this.sendWeeklyInformation = sendWeeklyInformation;

        this.queueManagerJDBC = queueManagerJDBC;
        this.messageCustomerService = messageCustomerService;
        this.userProfileManager = userProfileManager;
    }

    /** Create zip file of all the products for business store of Pharmacy Type. */
    @Scheduled(cron = "${loader.MessageAllUser.toAll}")
    public void message() {
        int count = queueManagerJDBC.countNumberOfUserServiced(7);
        //People Count * 2 hours / 24 hours for day /
        BigDecimal daysSaved = new BigDecimal(count)
            .multiply(new BigDecimal(2), MathContext.DECIMAL64)
            .divide(new BigDecimal(24), MathContext.DECIMAL64);

        String title = "Total days saved: " + daysSaved;
        String body = "NoQueue served " + count + " ESM in last 7 days. Man hours saved " + daysSaved + " days";

        LOG.info("Send message {} {} {}", title, body, sendWeeklyInformation);
        if ("OFF".equalsIgnoreCase(sendWeeklyInformation)) {
            return;
        }

        messageCustomerService.sendMessageToAll(title, body, userProfileManager.findOneByMail("admin@noqapp.com").getQueueUserId(), information);
    }
}
