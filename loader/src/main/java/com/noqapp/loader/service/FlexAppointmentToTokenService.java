package com.noqapp.loader.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.ScheduleAppointmentManager;
import com.noqapp.service.SystemNotifyOnGettingTokenService;
import com.noqapp.service.TokenQueueService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 7/6/21 5:29 PM
 */
@Service
public class FlexAppointmentToTokenService {
    private static final Logger LOG = LoggerFactory.getLogger(FlexAppointmentToTokenService.class);

    private SystemNotifyOnGettingTokenService systemNotifyOnGettingTokenService;
    private TokenQueueService tokenQueueService;

    private ScheduleAppointmentManager scheduleAppointmentManager;
    private BizStoreManager bizStoreManager;
    private RegisteredDeviceManager registeredDeviceManager;

    @Autowired
    public FlexAppointmentToTokenService(
        SystemNotifyOnGettingTokenService systemNotifyOnGettingTokenService,
        TokenQueueService tokenQueueService,

        ScheduleAppointmentManager scheduleAppointmentManager,
        BizStoreManager bizStoreManager,
        RegisteredDeviceManager registeredDeviceManager
    ) {
        this.systemNotifyOnGettingTokenService = systemNotifyOnGettingTokenService;
        this.tokenQueueService = tokenQueueService;

        this.scheduleAppointmentManager = scheduleAppointmentManager;
        this.bizStoreManager = bizStoreManager;
        this.registeredDeviceManager = registeredDeviceManager;
    }

    @Async
    public void changeFromFlexToWalkin(String codeQR, String scheduleDate, int startTime) {
        List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findBookedFlexAppointmentsForDay(codeQR, scheduleDate, startTime);
        LOG.info("Flex Appointment {} {} {} {}", codeQR, scheduleDate, startTime, scheduleAppointments.size());

        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            String registeredDeviceOfQid =  StringUtils.isNotBlank(scheduleAppointment.getGuardianQid())
                ? scheduleAppointment.getGuardianQid()
                : scheduleAppointment.getQueueUserId();
            RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(registeredDeviceOfQid);

            JsonToken jsonToken = tokenQueueService.getNextToken(
                registeredDevice.getDeviceId(),
                scheduleAppointment.getQueueUserId(),
                scheduleAppointment.getGuardianQid(),
                bizStore,
                TokenServiceEnum.S);

            /* Change the state if token is issued or not issued. */
            if (0 != jsonToken.getToken()) {
                scheduleAppointmentManager.changeAppointmentStatusOnTokenIssued(scheduleAppointment.getId());
            } else {
                scheduleAppointmentManager.changeAppointmentStatusOnTokenNotIssued(scheduleAppointment.getId(), jsonToken.getQueueJoinDenied());
            }

            systemNotifyOnGettingTokenService.notifyAfterGettingToken(bizStore, registeredDeviceOfQid, jsonToken);
        }
    }
}
