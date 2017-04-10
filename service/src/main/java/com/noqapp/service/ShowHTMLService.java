package com.noqapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noqapp.domain.BizStoreEntity;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public String showStore(String codeQR) {
        Map<String, String> rootMap = new HashMap<>();
        try {
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
            if (bizStore != null) {
                rootMap.put("bizName", bizStore.getBizName().getBusinessName());
                rootMap.put("storeAddress", bizStore.getAddressWrappedMore());
                rootMap.put("phone", bizStore.getPhoneFormatted());
                rootMap.put("displayName", bizStore.getDisplayName());
                rootMap.put("startHour", String.valueOf(bizStore.getStartHour()));
                rootMap.put("endHour", String.valueOf(bizStore.getEndHour()));

                return freemarkerService.freemarkerToString("html/show-store.ftl", rootMap);
            }

            return showStoreBlank;
        } catch (IOException | TemplateException e) {
            LOG.error("Failed generating html page for store reason={}", e.getLocalizedMessage(), e);
            return showStoreBlank;
        }
    }
}
