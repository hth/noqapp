package com.noqapp.service;

import org.apache.commons.lang3.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.utils.DateFormatter;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Generate store HTML page at runtime.
 *
 * User: hitender
 * Date: 1/16/17 9:24 AM
 */
@Service
public class ShowHTMLService {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private BizService bizService;
    private FreemarkerService freemarkerService;
    private TokenQueueService tokenQueueService;

    private static String showStoreBlank;

    @Autowired
    public ShowHTMLService(
            BizService bizService,
            FreemarkerService freemarkerService,
            TokenQueueService tokenQueueService
    ) {
        this.bizService = bizService;
        this.freemarkerService = freemarkerService;
        this.tokenQueueService = tokenQueueService;

        try {
            showStoreBlank = freemarkerService.freemarkerToString("html/show-store-blank.ftl", new HashMap<>());
        } catch (IOException | TemplateException e) {
            LOG.error("Failed generating html page for BLANK store reason={}", e.getLocalizedMessage(), e);
        }
    }

    public String showStoreByCodeQR(String codeQR) {
        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
        return showStoreByWebLocation(bizStore);
    }

    public String showStoreByWebLocation(BizStoreEntity bizStore) {
        Map<String, String> rootMap = new HashMap<>();
        try {
            if (populateStore(rootMap, bizStore)) {
                return freemarkerService.freemarkerToString("html/show-store.ftl", rootMap);
            }

            return showStoreBlank;
        } catch (IOException | TemplateException e) {
            LOG.error("Failed generating html page for store reason={}", e.getLocalizedMessage(), e);
            return showStoreBlank;
        }
    }

    private boolean populateStore(Map<String, String> rootMap, BizStoreEntity bizStore) throws IOException, TemplateException {
        if (null != bizStore) {
            bizStore.setStoreHours(bizService.finalAllStoreHours(bizStore.getId()));
            ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());
            
            rootMap.put("bizName", bizStore.getBizName().getBusinessName());
            rootMap.put("storeAddress", bizStore.getAddressWrappedMore());
            rootMap.put("phone", bizStore.getPhoneFormatted());
            rootMap.put("displayName", bizStore.getDisplayName());
            rootMap.put("dayOfWeek", WordUtils.capitalizeFully(zonedDateTime.getDayOfWeek().name()));
            rootMap.put("startHour", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getStartHour(zonedDateTime.getDayOfWeek())));
            rootMap.put("endHour", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getEndHour(zonedDateTime.getDayOfWeek())));
            rootMap.put("rating", String.valueOf(bizStore.getRating()));
            rootMap.put("ratingCount", String.valueOf(bizStore.getRatingCount()));

            TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(bizStore.getCodeQR());
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
        return false;
    }

    private void computeQueueStatus(Map<String, String> rootMap, ZonedDateTime zonedDateTime, StoreHourEntity storeHour) {
        //TODO(hth) check 0-23 or 1-24 hour format
        int currentZoneTime = Integer.valueOf(String.valueOf(zonedDateTime.getHour() + "" + zonedDateTime.getMinute()));
        if (storeHour.getTokenNotAvailableFrom() > currentZoneTime) {
            LOG.debug("{} > {}", storeHour.getTokenNotAvailableFrom(), currentZoneTime);
            rootMap.put("queueStatus", "Open");
        } else if (storeHour.getEndHour() < currentZoneTime) {
            LOG.debug("{} < {}", storeHour.getEndHour(), currentZoneTime);
            rootMap.put("queueStatus", "Closed");
        } else if (storeHour.getTokenNotAvailableFrom() < currentZoneTime && storeHour.getEndHour() > currentZoneTime) {
            LOG.debug("{} < {} & {} > {}", storeHour.getTokenNotAvailableFrom(), currentZoneTime, storeHour.getEndHour(), currentZoneTime);
            rootMap.put("queueStatus", "Closing soon. No more token accepted.");
        }
    }
}
