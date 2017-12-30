package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.MailTypeEnum;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.service.AccountService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.MailService;
import com.noqapp.service.StatsCronService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * hitender
 * 12/29/17 5:00 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class DailyRegistrationStatusMail {
    private static final Logger LOG = LoggerFactory.getLogger(DailyRegistrationStatusMail.class);

    private String registrationStatusSwitch;

    private AccountService accountService;
    private BusinessUserService businessUserService;
    private RegisteredDeviceManager registeredDeviceManager;
    private MailService mailService;
    private StatsCronService statsCronService;

    @Autowired
    public DailyRegistrationStatusMail(
            @Value("${DailyRegistrationStatusMail.registrationStatusSwitch}")
            String registrationStatusSwitch,

            AccountService accountService,
            BusinessUserService businessUserService,
            RegisteredDeviceManager registeredDeviceManager,
            MailService mailService,
            StatsCronService statsCronService
    ) {
        this.registrationStatusSwitch = registrationStatusSwitch;

        this.accountService = accountService;
        this.businessUserService = businessUserService;
        this.registeredDeviceManager = registeredDeviceManager;
        this.mailService = mailService;
        this.statsCronService = statsCronService;
    }

    /**
     * Delivers daily stats through email.
     */
    @Scheduled(cron = "${loader.DailyRegistrationStatusMail.registrationStatusMail}")
    public void registrationStatusMail() {
        StatsCronEntity statsCron = new StatsCronEntity(
                DailyRegistrationStatusMail.class.getName(),
                "RegistrationStatusMail",
                registrationStatusSwitch);

        if ("OFF".equalsIgnoreCase(registrationStatusSwitch)) {
            return;
        }

        long registeredUser = 0;
        long awaitingBusinessApproval = 0;
        long deviceRegistered = 0;
        long androidDeviceRegistered = 0;
        long iPhoneDeviceRegistered = 0;
        MailTypeEnum mailType;

        Date from = DateUtil.getDateMinusDay(1);
        Date to = DateUtil.nowMidnightDate();
        try {
            registeredUser = accountService.countRegisteredBetweenDates(from, to);
            awaitingBusinessApproval = businessUserService.awaitingBusinessApprovalCount();
            deviceRegistered = registeredDeviceManager.countRegisteredBetweenDates(from, to, null);
            androidDeviceRegistered = registeredDeviceManager.countRegisteredBetweenDates(from, to, DeviceTypeEnum.A);
            iPhoneDeviceRegistered = registeredDeviceManager.countRegisteredBetweenDates(from, to, DeviceTypeEnum.I);

            mailType = mailService.registrationStatusMail(
                    awaitingBusinessApproval,
                    registeredUser,
                    deviceRegistered,
                    androidDeviceRegistered,
                    iPhoneDeviceRegistered
            );

            if (mailType == MailTypeEnum.FAILURE) {
                throw new RuntimeException("Failed to send daily registration status mail");
            }
        } catch (Exception e) {
            LOG.error("Failed finding registration status from={} to={}, reason={}", from, to, e.getLocalizedMessage(), e);
        } finally {
            statsCron.addStats("registeredUser", registeredUser);
            statsCron.addStats("awaitingBusinessApproval", awaitingBusinessApproval);
            statsCron.addStats("deviceRegistered", deviceRegistered);
            statsCron.addStats("androidDeviceRegistered", androidDeviceRegistered);
            statsCron.addStats("iPhoneDeviceRegistered", iPhoneDeviceRegistered);
            statsCronService.save(statsCron);

            /* Without if condition its too noisy. */
            LOG.info("Registration Status from={} to={} registeredUser={} awaitingBusinessApproval={} deviceRegistered={}",
                    from,
                    to,
                    registeredUser,
                    awaitingBusinessApproval,
                    deviceRegistered);

        }
    }
}
