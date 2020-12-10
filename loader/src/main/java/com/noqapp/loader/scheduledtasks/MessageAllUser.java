package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.OutGoingNotificationEntity;
import com.noqapp.repository.OutGoingNotificationManager;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;

/**
 * hitender
 * 11/30/20 6:57 PM
 */
@Component
public class MessageAllUser {
    private static final Logger LOG = LoggerFactory.getLogger(MessageAllUser.class);

    private String sendWeeklyInformation;

    public QueueManagerJDBC queueManagerJDBC;
    public MessageCustomerService messageCustomerService;
    public UserProfileManager userProfileManager;
    public OutGoingNotificationManager outGoingNotificationManager;

    @Autowired
    public MessageAllUser(
        @Value("${makePreferredBusinessFiles:OFF}")
        String sendWeeklyInformation,

        QueueManagerJDBC queueManagerJDBC,
        MessageCustomerService messageCustomerService,
        UserProfileManager userProfileManager,
        OutGoingNotificationManager outGoingNotificationManager
    ) {
        this.sendWeeklyInformation = sendWeeklyInformation;

        this.queueManagerJDBC = queueManagerJDBC;
        this.messageCustomerService = messageCustomerService;
        this.userProfileManager = userProfileManager;
        this.outGoingNotificationManager = outGoingNotificationManager;
    }

    /** Send regular message to all users. */
    @Scheduled(cron = "${loader.MessageAllUser.toAll}")
    public void message() {
//        int count = queueManagerJDBC.countNumberOfUserServiced(7);
        //People Count * 2 hours / 24 hours for day /
//        BigDecimal daysSaved = new BigDecimal(count)
//            .multiply(new BigDecimal(2), MathContext.DECIMAL64)
//            .divide(new BigDecimal(24), MathContext.DECIMAL64)
//            .setScale(2, RoundingMode.HALF_UP);

//        String title = "Total days saved: " + daysSaved;
//        String body = "In last 7 days, NoQueue has helped save " + daysSaved + " man days. Thank you for giving us the opportunity to save your time.";

        LocalDate localDate = LocalDate.now();
        int weekYear = localDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        OutGoingNotificationEntity outGoingNotification = outGoingNotificationManager.findToSend(weekYear);

        LOG.info("Send message {} {} {}", outGoingNotification.getTitle(), outGoingNotification.getBody(), weekYear);
        if ("OFF".equalsIgnoreCase(sendWeeklyInformation)) {
            return;
        }

        messageCustomerService.sendMessageToAll(
            outGoingNotification.getTitle(),
            outGoingNotification.getBody(),
            userProfileManager.findOneByMail("admin@noqapp.com").getQueueUserId(),
            outGoingNotification.getTopic());
    }
}
