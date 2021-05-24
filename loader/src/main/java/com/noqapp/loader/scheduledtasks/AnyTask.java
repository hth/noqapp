package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.repository.RegisteredDeviceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

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

    private RegisteredDeviceManager registeredDeviceManager;

    @Autowired
    public AnyTask(
        @Value("${oneTimeStatusSwitch:ON}")
        String oneTimeStatusSwitch,

        Environment environment,
        RegisteredDeviceManager registeredDeviceManager
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.environment = environment;
        LOG.info("AnyTask environment={}", this.environment.getProperty("build.env"));

        this.registeredDeviceManager = registeredDeviceManager;
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
        try (Stream<RegisteredDeviceEntity> stream = registeredDeviceManager.findAll()) {
            stream.iterator().forEachRemaining(registeredDevice -> {
                try {
                    double[] coordinate = registeredDevice.getCoordinate();
                    GeoJsonPoint point = new GeoJsonPoint(coordinate[0], coordinate[1]);
                    registeredDeviceManager.addGeoPoint(registeredDevice.getId(), point);
                } catch (Exception e) {
                    LOG.error("Failed adding point processing for id={} reason={}",
                        registeredDevice.getId(),
                        e.getLocalizedMessage(),
                        e);
                }
            });
        }
    }
}
