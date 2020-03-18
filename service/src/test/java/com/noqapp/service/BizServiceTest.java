package com.noqapp.service;

import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessUserManager;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.ScheduledTaskManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.UserProfileManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * hitender
 * 2/1/18 3:24 PM
 */
class BizServiceTest {

    private double degreeInMiles;
    private double degreeInKilometers;

    @Mock private BizNameManager bizNameManager;
    @Mock private BizStoreManager bizStoreManager;
    @Mock private StoreHourManager storeHourManager;
    @Mock private TokenQueueService tokenQueueService;
    @Mock private QueueService queueService;
    @Mock private BusinessUserManager businessUserManager;
    @Mock private BusinessUserStoreManager businessUserStoreManager;
    @Mock private MailService mailService;
    @Mock private UserProfileManager userProfileManager;
    @Mock private ScheduledTaskManager scheduledTaskManager;

    private BizService bizService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        bizService = new BizService(
            69.172,
            111.321,
            bizNameManager,
            bizStoreManager,
            storeHourManager,
            tokenQueueService,
            queueService,
            businessUserManager,
            businessUserStoreManager,
            mailService,
            userProfileManager,
            scheduledTaskManager);
    }

    @Test
    void buildWebLocationForStore_WithOutBizNameLocation() {
        String wl = bizService.buildWebLocationForStore(
            "Jankpuri",
            "New Delhi",
            "MH",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse",
            null,
            null);
        Assertions.assertEquals("/in/dimmer-lime/jankpuri-new-delhi-mh/dr-mich-j-douse", wl);

        wl = bizService.buildWebLocationForStore(
            "Jankpuri",
            "New Delhi",
            "MH",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse (evening)",
            null,
            null);
        Assertions.assertEquals("/in/dimmer-lime/jankpuri-new-delhi-mh/dr-mich-j-douse-evening", wl);

        wl = bizService.buildWebLocationForStore(
            "",
            "New Delhi",
            "MH",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse",
            null,
            null);
        Assertions.assertEquals("/in/dimmer-lime/new-delhi-mh/dr-mich-j-douse", wl);

        wl = bizService.buildWebLocationForStore(
            "",
            "New Delhi",
            "MH",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse (evening)",
            null,
            null);
        Assertions.assertEquals("/in/dimmer-lime/new-delhi-mh/dr-mich-j-douse-evening", wl);

        wl = bizService.buildWebLocationForStore(
            "",
            "",
            "MH",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse",
            null,
            null);
        Assertions.assertEquals("/in/dimmer-lime/mh/dr-mich-j-douse", wl);

        wl = bizService.buildWebLocationForStore(
            "",
            "",
            "",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse",
            null,
            null);
        Assertions.assertEquals("/in/dimmer-lime/dr-mich-j-douse", wl);
    }

    @Test
    void buildWebLocationForStore_WithBizNameLocation() {
        String wl = bizService.buildWebLocationForStore(
            "Jankpuri",
            "New Delhi",
            "MH",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse",
            null,
            "/in/navi-mumbai-mh/ssd-hospital");
        Assertions.assertEquals("/in/navi-mumbai-mh/ssd-hospital/dr-mich-j-douse", wl);

        wl = bizService.buildWebLocationForStore(
            "Jankpuri",
            "New Delhi",
            "MH",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse (evening)",
            null,
            "/in/navi-mumbai-mh/ssd-hospital");
        Assertions.assertEquals("/in/navi-mumbai-mh/ssd-hospital/dr-mich-j-douse-evening", wl);

        wl = bizService.buildWebLocationForStore(
            "",
            "New Delhi",
            "MH",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse",
            null,
            "/in/navi-mumbai-mh/ssd-hospital");
        Assertions.assertEquals("/in/navi-mumbai-mh/ssd-hospital/dr-mich-j-douse", wl);

        wl = bizService.buildWebLocationForStore(
            "",
            "New Delhi",
            "MH",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse (evening)",
            null,
            "/in/navi-mumbai-mh/ssd-hospital");
        Assertions.assertEquals("/in/navi-mumbai-mh/ssd-hospital/dr-mich-j-douse-evening", wl);

        wl = bizService.buildWebLocationForStore(
            "",
            "",
            "MH",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse",
            null,
            "/in/navi-mumbai-mh/ssd-hospital");
        Assertions.assertEquals("/in/navi-mumbai-mh/ssd-hospital/dr-mich-j-douse", wl);

        wl = bizService.buildWebLocationForStore(
            "",
            "",
            "",
            "IN",
            "Dimmer Lime",
            "Dr. Mich J. Douse",
            null,
            "/in/navi-mumbai-mh/ssd-hospital");
        Assertions.assertEquals("/in/navi-mumbai-mh/ssd-hospital/dr-mich-j-douse", wl);
    }

    @Test
    void buildWebLocationForBiz() {
        String wl = bizService.buildWebLocationForBiz(
            "New Delhi",
            "MH",
            "IN",
            "Dimmer & Lime",
            null);
        Assertions.assertEquals("/in/new-delhi-mh/dimmer-lime", wl);

        wl = bizService.buildWebLocationForBiz(
            "",
            "MH",
            "IN",
            "Dimmer 12#$%&*()?><: Lime",
            null);
        Assertions.assertEquals("/in/mh/dimmer-lime", wl);

        wl = bizService.buildWebLocationForBiz(
            "",
            "",
            "IN",
            "Dimmer & Lime",
            null);
        Assertions.assertEquals("/in/dimmer-lime", wl);
    }
}
