package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.medical.domain.HospitalVisitScheduleEntity;
import com.noqapp.medical.repository.HospitalVisitScheduleManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.DeviceService;
import com.noqapp.service.MailService;
import com.noqapp.service.MessageCustomerService;
import com.noqapp.service.StatsCronService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 2019-07-22 09:23
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class HospitalUpComingVisitFCM {
    private static final Logger LOG = LoggerFactory.getLogger(HospitalUpComingVisitFCM.class);

    private String notifyVisitSwitch;

    private HospitalVisitScheduleManager hospitalVisitScheduleManager;
    private UserProfileManager userProfileManager;

    private MessageCustomerService messageCustomerService;
    private DeviceService deviceService;
    private MailService mailService;
    private StatsCronService statsCronService;

    @Autowired
    public HospitalUpComingVisitFCM(
        @Value("${HospitalUpComingVisitFCM.notifyVisit.switch:ON}")
        String notifyVisitSwitch,

        HospitalVisitScheduleManager hospitalVisitScheduleManager,
        UserProfileManager userProfileManager,

        MessageCustomerService messageCustomerService,
        DeviceService deviceService,
        MailService mailService,
        StatsCronService statsCronService
    ) {
        this.notifyVisitSwitch = notifyVisitSwitch;
        this.hospitalVisitScheduleManager = hospitalVisitScheduleManager;
        this.userProfileManager = userProfileManager;

        this.messageCustomerService = messageCustomerService;
        this.deviceService = deviceService;
        this.mailService = mailService;
        this.statsCronService = statsCronService;
    }

    @Scheduled(cron = "${loader.HospitalUpComingVisitFCM.notifyVisit}")
    public void notifyVisit() {
        StatsCronEntity statsCron = new StatsCronEntity(
            HospitalUpComingVisitFCM.class.getName(),
            "notifyVisit",
            notifyVisitSwitch);

        if ("OFF".equalsIgnoreCase(notifyVisitSwitch)) {
            return;
        }

        AtomicInteger notificationSentCount = new AtomicInteger();
        AtomicInteger mailSentCount = new AtomicInteger();
        try (Stream<HospitalVisitScheduleEntity> stream = hospitalVisitScheduleManager.notifyAllUpComingHospitalVisit()) {
            stream.iterator().forEachRemaining(hospitalVisitSchedule -> {
                String title = hospitalVisitSchedule.getHospitalVisitFor().getDescription() + " Reminder";
                String body = "Visit hospital on " + DateUtil.dateToString_UTC(hospitalVisitSchedule.getExpectedDate(), DateUtil.DTF_DD_MMM_YYYY)
                    + " for " + hospitalVisitSchedule.getHospitalVisitFor().getDescription() + ".\n\n"
                    + "Please book your appointment. For more details on upcoming hospital visit, click on Medical Profile."
                    + "\n\n"
                    + "Note: This message is auto-generated based on your Date of Birth set in your profile or dependent's profile or Hospital/Doctor has scheduled a visit.";
                RegisteredDeviceEntity registeredDevice = deviceService.findRegisteredDeviceByQid(hospitalVisitSchedule.getQueueUserId());
                messageCustomerService.sendMessageToSpecificUser(title, body, null, registeredDevice, MessageOriginEnum.A, BusinessTypeEnum.DO);
                hospitalVisitScheduleManager.increaseNotificationCount(hospitalVisitSchedule.getId());
                notificationSentCount.getAndIncrement();

                UserProfileEntity userProfile = userProfileManager.findByQueueUserId(hospitalVisitSchedule.getQueueUserId());
                UserProfileEntity userProfileGuardian = null;
                if (StringUtils.isNotBlank(userProfile.getGuardianPhone())) {
                    userProfileGuardian = userProfileManager.findOneByPhone(userProfile.getGuardianPhone());
                }

                Map<String, Object> rootMap = new HashMap<>();
                rootMap.put("profileName", userProfile.getName());
                rootMap.put("message", body);

                mailService.sendAnyMail(
                    userProfileGuardian == null ? userProfile.getEmail() : userProfileGuardian.getEmail(),
                    userProfileGuardian == null ? userProfile.getName() : userProfileGuardian.getName(),
                    title,
                    rootMap,
                    "mail/medical/hospitalVisit.ftl"
                );
                mailSentCount.getAndIncrement();
            });
        } catch (Exception e) {
            LOG.error("Failed sending business status mail to admins reason={}", e.getLocalizedMessage(), e);
        } finally {
            statsCron.addStats("notificationSentCount", notificationSentCount.get());
            statsCron.addStats("mailSentCount", mailSentCount.get());
            statsCronService.save(statsCron);

            /* Without if condition its too noisy. */
            LOG.info("Business Status Mail sentMail={} notificationSentCount={}", mailSentCount.get(), notificationSentCount.get());
        }
    }
}
