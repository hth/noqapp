package com.noqapp.service;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.utils.DateFormatter;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 1/16/17 9:24 AM
 */
@Service
public class ShowHTMLService {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private BizService bizService;
    private FreemarkerService freemarkerService;

    private static String showStoreBlank;

    @Autowired
    public ShowHTMLService(BizService bizService, FreemarkerService freemarkerService) {
        this.bizService = bizService;
        this.freemarkerService = freemarkerService;

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
            rootMap.put("dayOfWeek", StringUtils.capitalize(zonedDateTime.getDayOfWeek().name()));
            rootMap.put("startHour", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getStartHour(zonedDateTime.getDayOfWeek())));
            rootMap.put("endHour", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getEndHour(zonedDateTime.getDayOfWeek())));
            rootMap.put("rating", String.valueOf(bizStore.getRating()));

            return true;
        }
        return false;
    }
}
