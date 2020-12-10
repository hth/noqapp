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
        @Value("${MessageAllUser.makePreferredBusinessFiles}")
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

        if (null != outGoingNotification) {
            LOG.info("Send message {} {} {}", outGoingNotification.getTitle(), outGoingNotification.getBody(), weekYear);
            if ("OFF".equalsIgnoreCase(sendWeeklyInformation)) {
                return;
            }

            int sendCount = messageCustomerService.sendMessageToAll(
                outGoingNotification.getTitle(),
                outGoingNotification.getBody(),
                userProfileManager.findByQueueUserId("100000000001").getQueueUserId(),
                outGoingNotification.getTopic());

            if (sendCount > 0) {
                outGoingNotification.inActive();
                outGoingNotification.setSent(true).inActive();
            }
        } else {
            outGoingNotification = new OutGoingNotificationEntity()
                .setTitle("Become Business Outreach Ambassador")
                .setBody("Earn upto Rs 1,00,000 by bringing new businesses on NoQueue. Limited availability. For more information, send email with phone number to boa@noqapp.com")
                .setTopic("i")
                .setWeekYear(40)
                .setSent(false);

        }
        outGoingNotificationManager.save(outGoingNotification);
    }
}
