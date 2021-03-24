package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.UserAddressEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.JsonUserAddress;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.domain.shared.Geocode;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.ExternalService;
import com.noqapp.service.UserAddressService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.util.CloseableIterator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mostly used one time to update, modify any data.
 *
 * hitender
 * 1/13/18 6:17 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class AnyTask {
    private static final Logger LOG = LoggerFactory.getLogger(AnyTask.class);

    private String oneTimeStatusSwitch;

    private Environment environment;
    private UserProfileManager userProfileManager;
    private UserAddressService userAddressService;
    private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    private ExternalService externalService;

    @Autowired
    public AnyTask(
        @Value("${oneTimeStatusSwitch:ON}")
        String oneTimeStatusSwitch,

        Environment environment,
        UserProfileManager userProfileManager,
        UserAddressService userAddressService,
        PurchaseOrderManagerJDBC purchaseOrderManagerJDBC,
        ExternalService externalService
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.environment = environment;
        this.userProfileManager = userProfileManager;
        this.userAddressService = userAddressService;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;
        this.externalService = externalService;
        LOG.info("AnyTask environment={}", this.environment.getProperty("build.env"));
    }

    /**
     * Runs any requested task underneath.
     * Make sure there are proper locks, limits and or conditions to prevent re-run.
     */
    @SuppressWarnings("all")
    @Scheduled(fixedDelayString = "${loader.MailProcess.sendMail}")
    public void someTask() {
        if ("OFF".equalsIgnoreCase(oneTimeStatusSwitch)) {
            return;
        }

        oneTimeStatusSwitch = "OFF";
        LOG.info("Run someTask in AnyTask");

        /* Write your method after here. Un-comment @Scheduled. */
        int profileToAddress = 0;
        int userAddressCount = 0;
        int purchaseOrderAddress = 0, purchaseOrderAddressFailed = 0;
        CloseableIterator<UserProfileEntity> stream = userProfileManager.findAllWithAddress();
        while (stream.hasNext()) {
            UserProfileEntity userProfile = stream.next();
            try {
                String updatedAddressTo = userProfile.getAddress();

                Geocode geocode = Geocode.newInstance(externalService.getGeocodingResults(updatedAddressTo), updatedAddressTo);
                DecodedAddress decodedAddress = DecodedAddress.newInstance(geocode.getResults(), 0);

                JsonUserAddress jsonUserAddress = new JsonUserAddress()
                    .setAddress(updatedAddressTo)
                    .setArea(decodedAddress.getArea())
                    .setTown(decodedAddress.getTown())
                    .setDistrict(decodedAddress.getDistrict())
                    .setState(decodedAddress.getState())
                    .setStateShortName(decodedAddress.getStateShortName())
                    .setCountryShortName(decodedAddress.getCountryShortName())
                    .setLatitude(String.valueOf(decodedAddress.getCoordinate()[1]))
                    .setLongitude(String.valueOf(decodedAddress.getCoordinate()[0]));

                UserAddressEntity userAddress = userAddressService.saveAddress(
                    CommonUtil.generateHexFromObjectId(),
                    userProfile.getQueueUserId(),
                    jsonUserAddress
                );

                profileToAddress ++;
                LOG.info("Added address={} {} {}", userAddress.getAddress(), userAddress.getQueueUserId(), userAddress.getId());
            } catch (Exception e) {
                LOG.error("Failed {} {} {} {}", userProfile.getId(), userProfile.getQueueUserId(), userProfile.getAddress(), e.getLocalizedMessage(), e);
                userProfileManager.unsetAddress(userProfile.getQueueUserId());
            }
        }

        //Updated UserAddress
        List<UserAddressEntity> userAddresses = userAddressService.findAllWhereCoordinateDoesNotExists();
        for (UserAddressEntity userAddress1 : userAddresses) {
            try {
                String updatedAddressTo = userAddress1.getAddress();

                Geocode geocode = Geocode.newInstance(externalService.getGeocodingResults(updatedAddressTo), updatedAddressTo);
                DecodedAddress decodedAddress = DecodedAddress.newInstance(geocode.getResults(), 0);

                JsonUserAddress jsonUserAddress = new JsonUserAddress()
                    .setAddress(updatedAddressTo)
                    .setArea(decodedAddress.getArea())
                    .setTown(decodedAddress.getTown())
                    .setDistrict(decodedAddress.getDistrict())
                    .setState(decodedAddress.getState())
                    .setStateShortName(decodedAddress.getStateShortName())
                    .setCountryShortName(decodedAddress.getCountryShortName())
                    .setLatitude(String.valueOf(decodedAddress.getCoordinate()[1]))
                    .setLongitude(String.valueOf(decodedAddress.getCoordinate()[0]));

                UserAddressEntity userAddress = userAddressService.updateAddress(
                    userAddress1.getId(),
                    userAddress1.getQueueUserId(),
                    jsonUserAddress
                );

                userAddressCount ++;
                LOG.info("Updated address={} {} {}", userAddress.getAddress(), userAddress.getQueueUserId(), userAddress.getId());
            } catch (Exception e) {
                LOG.error("Failed updating address {} {} {} {}", userAddress1.getId(), userAddress1.getQueueUserId(), userAddress1.getAddress(), e.getLocalizedMessage(), e);
            }
        }

        List<PurchaseOrderEntity> purchaseOrders = purchaseOrderManagerJDBC.findAllOrdersWhereAddressExists();
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            try {
                String updatedAddressTo = purchaseOrder.getDeliveryAddress();

                Geocode geocode = Geocode.newInstance(externalService.getGeocodingResults(updatedAddressTo), updatedAddressTo);
                DecodedAddress decodedAddress = DecodedAddress.newInstance(geocode.getResults(), 0);

                JsonUserAddress jsonUserAddress = new JsonUserAddress()
                    .setAddress(updatedAddressTo)
                    .setArea(decodedAddress.getArea())
                    .setTown(decodedAddress.getTown())
                    .setDistrict(decodedAddress.getDistrict())
                    .setState(decodedAddress.getState())
                    .setStateShortName(decodedAddress.getStateShortName())
                    .setCountryShortName(decodedAddress.getCountryShortName())
                    .setLatitude(String.valueOf(decodedAddress.getCoordinate()[1]))
                    .setLongitude(String.valueOf(decodedAddress.getCoordinate()[0]));

                UserAddressEntity userAddress = userAddressService.saveAddress(
                    CommonUtil.generateHexFromObjectId(),
                    purchaseOrder.getQueueUserId(),
                    jsonUserAddress
                );

                boolean updatedSuccessfully = purchaseOrderManagerJDBC.updateAddressToUserAddressId(purchaseOrder.getId(), userAddress.getId());
                if (updatedSuccessfully) {
                    purchaseOrderAddress++;
                } else {
                    purchaseOrderAddressFailed++;
                }
                LOG.info("Updated address={} {} {} {}", userAddress.getAddress(), userAddress.getQueueUserId(), userAddress.getId(), updatedSuccessfully);
            } catch (Exception e) {
                LOG.error("Failed updating address {} {} {} {}", purchaseOrder.getId(), purchaseOrder.getQueueUserId(), purchaseOrder.getDeliveryAddress(), e.getLocalizedMessage(), e);
            }
        }

        LOG.info("Result profileToAddress={} userAddressCount={} purchaseOrderAddress={} purchaseOrderAddressFailed={}", profileToAddress, userAddressCount, purchaseOrderAddress, purchaseOrderAddressFailed);
    }
}
