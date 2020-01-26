package com.noqapp.service;

import static com.noqapp.service.ProfessionalProfileService.POPULATE_PROFILE.PUBLIC;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.JsonProfessionalProfile;
import com.noqapp.domain.types.UserLevelEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import freemarker.template.TemplateException;

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
public class ShowProfessionalProfileHTMLService {
    private static final Logger LOG = LoggerFactory.getLogger(ShowProfessionalProfileHTMLService.class);

    private BizService bizService;
    private FreemarkerService freemarkerService;
    private ShowHTMLService showHTMLService;
    private ProfessionalProfileService professionalProfileService;
    private BusinessUserStoreService businessUserStoreService;
    private AccountService accountService;

    private static String showStoreBlank;
    private static String showBusinessBlank;

    @Autowired
    public ShowProfessionalProfileHTMLService(
        @Value("${parentHost}")
        String parentHost,

        @Value ("${domain}")
        String domain,

        @Value ("${https}")
        String https,

        BizService bizService,
        FreemarkerService freemarkerService,
        ShowHTMLService showHTMLService,
        ProfessionalProfileService professionalProfileService,
        BusinessUserStoreService businessUserStoreService,
        AccountService accountService
    ) {
        this.bizService = bizService;
        this.freemarkerService = freemarkerService;
        this.showHTMLService = showHTMLService;
        this.professionalProfileService = professionalProfileService;
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
                try {
                    Map<String, Object> rootMap = new HashMap<>();
                    showHTMLService.populateStore(rootMap, selectedBizStore);
                    return freemarkerService.freemarkerToString("html/show-store.ftl", rootMap);
                } catch (IOException | TemplateException e) {
                    LOG.error("Failed generating html page for store blank {} {} reason={}",
                        selectedBizStore.getDisplayName(), selectedBizStore.getCodeQR(), e.getLocalizedMessage(), e);
                    return showStoreBlank;
                }
            } else {
                BusinessUserStoreEntity businessUserStore = businessUserStores.get(0);
                JsonProfessionalProfile jsonProfessionalProfile = professionalProfileService.getJsonProfessionalProfile(
                    businessUserStore.getQueueUserId(),
                    PUBLIC);

                List<BizStoreEntity> bizStores = new ArrayList<>();
                Set<String> managersAtStoreCodeQRs = jsonProfessionalProfile.getManagerAtStoreCodeQRs();
                if (!managersAtStoreCodeQRs.isEmpty()) {
                    for (String managersAtStoreCodeQR : managersAtStoreCodeQRs) {
                        BizStoreEntity bizStore = bizService.findByCodeQR(managersAtStoreCodeQR);
                        bizStores.add(bizStore);
                    }
                }
                UserProfileEntity userProfile = accountService.findProfileByQueueUserId(businessUserStore.getQueueUserId());

                /* Not store assigned. No profile created. */
                if (bizStores.isEmpty()) {
                    LOG.warn("No store assigned to Dr qid={} userLevel={} profile={}",
                        userProfile.getQueueUserId(), userProfile.getLevel(), userProfile.getName());
                    return showStoreBlank;
                }

                try {
                    Map<String, Map<String, Object>> rootMap = new HashMap<>();
                    showHTMLService.populateMedicalProfile(rootMap, userProfile, jsonProfessionalProfile, bizStores);
                    return freemarkerService.freemarkerToStringComplex("html/show-store-healthCare.ftl", rootMap);
                } catch (IOException | TemplateException e) {
                    LOG.error("Failed generating html page for store userProfile={} reason={}",
                        userProfile.getQueueUserId(), e.getLocalizedMessage(), e);
                    return showStoreBlank;
                }
            }
        } catch (NullPointerException e) {
            LOG.error("Failed generating html page for store reason={}", e.getLocalizedMessage(), e);
            return showStoreBlank;
        }
    }
}
