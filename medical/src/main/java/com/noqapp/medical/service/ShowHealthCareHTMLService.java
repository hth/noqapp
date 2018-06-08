package com.noqapp.medical.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.medical.JsonHealthCareProfile;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.FreemarkerService;
import com.noqapp.service.ShowHTMLService;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * hitender
 * 6/3/18 12:15 AM
 */
@Service
public class ShowHealthCareHTMLService {
    private static final Logger LOG = LoggerFactory.getLogger(ShowHealthCareHTMLService.class);

    private String parentHost;
    private String domain;
    private String https;

    private BizService bizService;
    private FreemarkerService freemarkerService;
    private ShowHTMLService showHTMLService;
    private HealthCareProfileService healthCareProfileService;
    private BusinessUserStoreService businessUserStoreService;
    private AccountService accountService;

    private static String showStoreBlank;
    private static String showBusinessBlank;

    @Autowired
    public ShowHealthCareHTMLService(
            @Value("${parentHost}")
            String parentHost,

            @Value ("${domain}")
            String domain,

            @Value ("${https}")
            String https,

            BizService bizService,
            FreemarkerService freemarkerService,
            ShowHTMLService showHTMLService,
            HealthCareProfileService healthCareProfileService,
            BusinessUserStoreService businessUserStoreService,
            AccountService accountService
    ) {
        this.parentHost = parentHost;
        this.domain = domain;
        this.https = https;

        this.bizService = bizService;
        this.freemarkerService = freemarkerService;
        this.showHTMLService = showHTMLService;
        this.healthCareProfileService = healthCareProfileService;
        this.businessUserStoreService = businessUserStoreService;
        this.accountService = accountService;

        try {
            Map<String, Object> rootMap = new HashMap<>();
            rootMap.put("parentHost", parentHost);
            rootMap.put("domain", domain);
            rootMap.put("https", https);
            showStoreBlank = freemarkerService.freemarkerToString("html/show-store-blank.ftl", rootMap);
            showBusinessBlank = freemarkerService.freemarkerToString("html/show-business-blank.ftl", rootMap);
        } catch (IOException | TemplateException e) {
            LOG.error("Failed generating html page for BLANK store reason={}", e.getLocalizedMessage(), e);
        }
    }

    public String showStoreByWebLocation(BizStoreEntity selectedBizStore) {
        try {
            if (null == selectedBizStore) {
                LOG.warn("No such store found. Showing blank store.");
                return showStoreBlank;
            }

            List<BusinessUserStoreEntity> businessUserStores = businessUserStoreService.findAllManagingStoreWithUserLevel(
                    selectedBizStore.getId(),
                    UserLevelEnum.S_MANAGER);

            if (businessUserStores.isEmpty()) {
                Map<String, Object> rootMap = new HashMap<>();
                showHTMLService.populateStore(rootMap, selectedBizStore);
                return freemarkerService.freemarkerToString("html/show-store.ftl", rootMap);
            } else {
                BusinessUserStoreEntity businessUserStore = businessUserStores.get(0);
                JsonHealthCareProfile jsonHealthCareProfile = healthCareProfileService.getJsonHealthCareProfileByQid(businessUserStore.getQueueUserId());

                List<BizStoreEntity> bizStores = new ArrayList<>();
                Set<String> managersAtStoreCodeQRs = jsonHealthCareProfile.getManagerAtStoreCodeQRs();
                if (!managersAtStoreCodeQRs.isEmpty()) {
                    for (String managersAtStoreCodeQR : managersAtStoreCodeQRs) {
                        BizStoreEntity bizStore = bizService.findByCodeQR(managersAtStoreCodeQR);
                        bizStores.add(bizStore);
                    }
                }
                UserProfileEntity userProfile = accountService.findProfileByQueueUserId(businessUserStore.getQueueUserId());

                Map<String, Map<String, Object>> rootMap = new HashMap<>();
                showHTMLService.populateMedicalProfile(rootMap, userProfile, jsonHealthCareProfile, bizStores);
                return freemarkerService.freemarkerToStringComplex("html/show-store-healthCare.ftl", rootMap);
            }
        } catch (IOException | TemplateException | NullPointerException e) {
            LOG.error("Failed generating html page for store reason={}", e.getLocalizedMessage(), e);
            return showStoreBlank;
        }
    }
}
