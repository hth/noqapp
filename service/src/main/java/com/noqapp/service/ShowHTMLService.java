package com.noqapp.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.common.utils.DateFormatter;
import freemarker.template.TemplateException;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Generate store HTML page at runtime.
 *
 * User: hitender
 * Date: 1/16/17 9:24 AM
 */
@Service
public class ShowHTMLService {
    private static final Logger LOG = LoggerFactory.getLogger(ShowHTMLService.class);

    private String parentHost;

    private BizService bizService;
    private FreemarkerService freemarkerService;
    private TokenQueueService tokenQueueService;

    private static String showStoreBlank;

    @Autowired
    public ShowHTMLService(
            @Value("${parentHost}")
            String parentHost,

            BizService bizService,
            FreemarkerService freemarkerService,
            TokenQueueService tokenQueueService
    ) {
        this.parentHost = parentHost;

        this.bizService = bizService;
        this.freemarkerService = freemarkerService;
        this.tokenQueueService = tokenQueueService;

        try {
            Map<String, String> rootMap = new HashMap<>();
            rootMap.put("parentHost", parentHost);
            showStoreBlank = freemarkerService.freemarkerToString("html/show-store-blank.ftl", rootMap);
        } catch (IOException | TemplateException e) {
            LOG.error("Failed generating html page for BLANK store reason={}", e.getLocalizedMessage(), e);
        }
    }

    public String showStoreByCodeQR(String codeQR) {
        return showStoreByWebLocation(bizService.findByCodeQR(codeQR));
    }

    public String showStoreByWebLocation(BizStoreEntity bizStore) {
        Map<String, String> rootMap = new HashMap<>();
        try {
            if (null == bizStore) {
                LOG.warn("No such store found. Showing blank store.");
                return showStoreBlank;
            }

            if (populateStore(rootMap, bizStore)) {
                return freemarkerService.freemarkerToString("html/show-store.ftl", rootMap);
            } else {
                /* This can happen when the business is awaiting approval. */
                LOG.warn("Skipped creating store html bizStore={} bizName={} active={}",
                        bizStore.getId(),
                        bizStore.getBizName().getId(),
                        bizStore.isActive());
            }

            return showStoreBlank;
        } catch (IOException | TemplateException | NullPointerException e) {
            LOG.error("Failed generating html page for store reason={}", e.getLocalizedMessage(), e);
            return showStoreBlank;
        }
    }

    private boolean populateStore(
            Map<String, String> rootMap,
            BizStoreEntity bizStore
    ) {
        
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(bizStore.getCodeQR());

        if (null == tokenQueue) {
            /* This can happen when the business is awaiting approval. */
            LOG.warn("Could not find tokenQueue for codeQR={} active={}", bizStore.getCodeQR(), bizStore.isActive());
            return false;
        }

        bizStore.setStoreHours(bizService.findAllStoreHours(bizStore.getId()));
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());

        rootMap.put("parentHost", parentHost);
        rootMap.put("bizName", bizStore.getBizName().getBusinessName());
        rootMap.put("storeAddress", bizStore.getAddressWrappedMore());
        rootMap.put("phone", bizStore.getPhoneFormatted());
        rootMap.put("displayName", bizStore.getDisplayName());
        rootMap.put("dayOfWeek", WordUtils.capitalizeFully(zonedDateTime.getDayOfWeek().name()));
        rootMap.put("startHour", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getStartHour(zonedDateTime.getDayOfWeek())));
        rootMap.put("endHour", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getEndHour(zonedDateTime.getDayOfWeek())));
        rootMap.put("rating", String.valueOf(bizStore.getRatingFormatted()));
        rootMap.put("ratingCount", String.valueOf(bizStore.getRatingCount()));
        rootMap.put("peopleInQueue", String.valueOf(tokenQueue.numberOfPeopleInQueue()));

        int i = zonedDateTime.getDayOfWeek().getValue();
        StoreHourEntity storeHour = bizStore.getStoreHours().get(i - 1);
        if (storeHour.isDayClosed()) {
            rootMap.put("queueStatus", "Closed");
            rootMap.put("currentlyServing", "NA");
        } else {
            switch (tokenQueue.getQueueStatus()) {
                case S:
                    rootMap.put("queueStatus", "Not yet started");
                    rootMap.put("currentlyServing", "0");
                    break;
                case R:
                    rootMap.put("currentlyServing", "Next to serve " + tokenQueue.getLastNumber());
                    computeQueueStatus(rootMap, zonedDateTime, storeHour);
                    break;
                case N:
                    rootMap.put("currentlyServing", "Serving " + tokenQueue.getCurrentlyServing());
                    computeQueueStatus(rootMap, zonedDateTime, storeHour);
                    break;
                case D:
                    rootMap.put("currentlyServing", "Last served " + tokenQueue.getCurrentlyServing());
                    computeQueueStatus(rootMap, zonedDateTime, storeHour);
                    break;
                case C:
                    rootMap.put("queueStatus", "Closed Permanently");
                    rootMap.put("currentlyServing", "NA");
                    break;
                default:
                    LOG.error("Reached unreachable condition {}", tokenQueue.getQueueStatus());
                    throw new UnsupportedOperationException("Reached unreachable condition");
            }
        }
        return true;
    }

    private void computeQueueStatus(
            Map<String, String> rootMap,
            ZonedDateTime zonedDateTime,
            StoreHourEntity storeHour
    ) {
        /*
         * Hour format is 0-23, example 1 for 12:01 AM and 2359 for 11:59 PM.
         * Hence matches ZonedDateTime Hour and Minutes
         * And, To make sure minute in time 11:06 AM is not represented as 116 but as 1106 hence string formatting.
         */
        int timeIn24HourFormat = CommonUtil.getTimeIn24HourFormat(zonedDateTime);
        if (storeHour.getTokenNotAvailableFrom() > timeIn24HourFormat) {
            LOG.debug("{} > {}",
                    storeHour.getTokenNotAvailableFrom(),
                    timeIn24HourFormat);

            rootMap.put("queueStatus", "Open");

        } else if (storeHour.getEndHour() <= timeIn24HourFormat) {
            LOG.debug("{} < {}",
                    storeHour.getEndHour(),
                    timeIn24HourFormat);

            rootMap.put("queueStatus", "Closed");

        } else if (storeHour.getTokenNotAvailableFrom() < timeIn24HourFormat && storeHour.getEndHour() > timeIn24HourFormat) {
            LOG.debug("{} < {} & {} > {}",
                    storeHour.getTokenNotAvailableFrom(),
                    timeIn24HourFormat,
                    storeHour.getEndHour(),
                    timeIn24HourFormat);

            rootMap.put("queueStatus", "Closing soon. No more token accepted.");
        } else {
            LOG.error("QueueStatus computed currentZoneTime={} bizStoreId={} storeHour={}",
                    timeIn24HourFormat,
                    storeHour.getBizStoreId(),
                    storeHour);

            throw new UnsupportedOperationException("Reached unreachable condition");
        }
    }
}
