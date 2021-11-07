package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.types.AppFlavorEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.MailTypeEnum;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.service.AccountService;
import com.noqapp.service.AdvertisementService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.MailService;
import com.noqapp.service.PublishArticleService;
import com.noqapp.service.StatsCronService;
import com.noqapp.service.market.HouseholdItemService;
import com.noqapp.service.market.PropertyRentalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * hitender
 * 12/29/17 5:00 PM
 */
@SuppressWarnings({
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
    private PublishArticleService publishArticleService;
    private AdvertisementService advertisementService;
    private RegisteredDeviceManager registeredDeviceManager;
    private MailService mailService;
    private PropertyRentalService propertyRentalService;
    private HouseholdItemService householdItemService;
    private StatsCronService statsCronService;

    @Autowired
    public DailyRegistrationStatusMail(
        @Value("${DailyRegistrationStatusMail.registrationStatusSwitch}")
        String registrationStatusSwitch,

        AccountService accountService,
        BusinessUserService businessUserService,
        PublishArticleService publishArticleService,
        AdvertisementService advertisementService,
        RegisteredDeviceManager registeredDeviceManager,
        MailService mailService,
        PropertyRentalService propertyRentalService,
        HouseholdItemService householdItemService,
        StatsCronService statsCronService
    ) {
        this.registrationStatusSwitch = registrationStatusSwitch;

        this.accountService = accountService;
        this.businessUserService = businessUserService;
        this.publishArticleService = publishArticleService;
        this.advertisementService = advertisementService;
        this.registeredDeviceManager = registeredDeviceManager;
        this.mailService = mailService;
        this.propertyRentalService = propertyRentalService;
        this.householdItemService = householdItemService;
        this.statsCronService = statsCronService;
    }

    /**
     * Delivers daily stats through email.
     */
    @Scheduled(cron = "${loader.DailyRegistrationStatusMail.registrationStatusMail}")
    public void registrationStatusMail() {
        StatsCronEntity statsCron = new StatsCronEntity(
            DailyRegistrationStatusMail.class.getName(),
            "registrationStatusMail",
            registrationStatusSwitch);

        if ("OFF".equalsIgnoreCase(registrationStatusSwitch)) {
            return;
        }

        long registeredUser = 0;
        long awaitingBusinessApproval = 0;
        long awaitingPublishArticleApproval = 0;
        long awaitingAdvertisementApproval = 0;
        long deviceRegistered = 0;
        long androidDeviceRegistered = 0;
        long iPhoneDeviceRegistered = 0;
        long pendingMarketplaceApproval = 0;
        MailTypeEnum mailType;

        Date from = DateUtil.minusDays(1);
        Date to = DateUtil.nowMidnightDate();
        try {
            registeredUser = accountService.countRegisteredBetweenDates(from, to);
            awaitingBusinessApproval = businessUserService.awaitingBusinessApprovalCount();
            awaitingPublishArticleApproval = publishArticleService.findPendingApprovalCount();
            awaitingAdvertisementApproval = advertisementService.findApprovalPendingAdvertisementCount();
            pendingMarketplaceApproval = propertyRentalService.findAllPendingApprovalCount() + householdItemService.findAllPendingApprovalCount();
            deviceRegistered = registeredDeviceManager.countRegisteredBetweenDates(from, to, null);
            androidDeviceRegistered = registeredDeviceManager.countRegisteredBetweenDates(from, to, DeviceTypeEnum.A);
            Map<String, Long> androidFlavoredDevices = new LinkedHashMap<>();
            for (AppFlavorEnum appFlavor : AppFlavorEnum.values()) {
                androidFlavoredDevices.put(
                    appFlavor.getDescription(),
                    registeredDeviceManager.countRegisteredBetweenDates(from, to, DeviceTypeEnum.A, appFlavor));
            }

            iPhoneDeviceRegistered = registeredDeviceManager.countRegisteredBetweenDates(from, to, DeviceTypeEnum.I);
            Map<String, Long> iPhoneFlavoredDevices = new LinkedHashMap<>();
            for (AppFlavorEnum appFlavor : AppFlavorEnum.values()) {
                iPhoneFlavoredDevices.put(
                    appFlavor.getDescription(),
                    registeredDeviceManager.countRegisteredBetweenDates(from, to, DeviceTypeEnum.I, appFlavor));
            }

            mailType = mailService.registrationStatusMail(
                awaitingBusinessApproval,
                awaitingPublishArticleApproval,
                awaitingAdvertisementApproval,
                pendingMarketplaceApproval,
                registeredUser,
                deviceRegistered,
                androidDeviceRegistered,
                androidFlavoredDevices,
                iPhoneDeviceRegistered,
                iPhoneFlavoredDevices
            );

            if (mailType == MailTypeEnum.FAILURE) {
                throw new RuntimeException("Failed to send daily registration status mail");
            }
        } catch (Exception e) {
            LOG.error("Failed finding registration status from={} to={}, reason={}", from, to, e.getLocalizedMessage(), e);
        } finally {
            statsCron.addStats("registeredUser", registeredUser);
            statsCron.addStats("awaitingBusinessApproval", awaitingBusinessApproval);
            statsCron.addStats("awaitingPublishArticleApproval", awaitingPublishArticleApproval);
            statsCron.addStats("awaitingAdvertisementApproval", awaitingAdvertisementApproval);
            statsCron.addStats("deviceRegistered", deviceRegistered);
            statsCron.addStats("androidDeviceRegistered", androidDeviceRegistered);
            statsCron.addStats("iPhoneDeviceRegistered", iPhoneDeviceRegistered);
            statsCronService.save(statsCron);

            /* Without if condition it's too noisy. */
            LOG.info(
                "Registration Status " +
                "from={} " +
                "to={} " +
                "registeredUser={} " +
                "awaitingBusinessApproval={} " +
                "awaitingPublishArticleApproval={} " +
                "awaitingAdvertisementApproval={} " +
                "deviceRegistered={}",
                from,
                to,
                registeredUser,
                awaitingBusinessApproval,
                awaitingPublishArticleApproval,
                awaitingAdvertisementApproval,
                deviceRegistered);
        }
    }
}
