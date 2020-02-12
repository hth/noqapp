package com.noqapp.view.controller.business.documentation;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedPerson;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.medical.domain.json.JsonMedicalRecord;
import com.noqapp.medical.service.MedicalRecordService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.QueueService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.form.business.MedicalDocumentUploadForm;
import com.noqapp.view.form.business.MedicalDocumentUploadListForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 2/11/20 12:28 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/documentation/medical")
public class MedicalDocumentationController {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalDocumentationController.class);

    private String nextPage;
    private String uploadMedicalDocumentPage;

    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;
    private QueueService queueService;
    private BizService bizService;
    private MedicalRecordService medicalRecordService;
    private TokenQueueService tokenQueueService;

    @Autowired
    public MedicalDocumentationController(
        @Value("${nextPage:/business/documentation/medical/landing}")
        String nextPage,

        @Value("${uploadMedicalDocumentPage:/business/documentation/medical/uploadMedicalDocument}")
        String uploadMedicalDocumentPage,

        BusinessUserService businessUserService,
        BusinessUserStoreService businessUserStoreService,
        QueueService queueService,
        BizService bizService,
        MedicalRecordService medicalRecordService,
        TokenQueueService tokenQueueService
    ) {
        this.nextPage = nextPage;
        this.uploadMedicalDocumentPage = uploadMedicalDocumentPage;

        this.businessUserService = businessUserService;
        this.businessUserStoreService = businessUserStoreService;
        this.queueService = queueService;
        this.bizService = bizService;
        this.medicalRecordService = medicalRecordService;
        this.tokenQueueService = tokenQueueService;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String medicalLanding(
        @ModelAttribute("medicalDocumentUploadListForm")
        MedicalDocumentUploadListForm medicalDocumentUploadListForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on discount page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        medicalDocumentUploadListForm.setBusinessName(businessUser.getBizName().getBusinessName());
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreService.findAllStoreQueueAssociated(queueUser.getQueueUserId());
        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            String codeQR = businessUserStore.getCodeQR();
            JsonQueuePersonList jsonQueuePersonList = queueService.findAllClient(codeQR);
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
            StoreHourEntity storeHour = bizService.getStoreHours(codeQR, bizStore);
            bizStore.setStoreHours(new ArrayList<StoreHourEntity>() {{add(storeHour);}});
            medicalDocumentUploadListForm.addMedicalDocumentUploadForms(
                new MedicalDocumentUploadForm()
                    .setBizStore(bizStore)
                    .setJsonQueuePersonList(jsonQueuePersonList));
        }
        return nextPage;
    }

    @GetMapping(value = "/{recordReferenceId}/upload/{codeQR}", produces = "text/html;charset=UTF-8")
    public String uploadDocument(
        @PathVariable("recordReferenceId")
        ScrubbedInput recordReferenceId,

        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on discount page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        String identifier = new String(Base64.getDecoder().decode(recordReferenceId.getText()), StandardCharsets.ISO_8859_1);
        String[] recordReference = identifier.split("#");

        int token = Integer.parseInt(recordReference[0]);
        String qid = recordReference[1];
        String recordOwner = recordReference[2];

        QueueEntity queue = tokenQueueService.findOne(codeQR.getText(), token);
        model.addAttribute("queue", queue);

        JsonQueuedPerson jsonQueuedPerson = queueService.getJsonQueuedPerson(queue);
        model.addAttribute("jsonQueuedPerson", jsonQueuedPerson);

        JsonMedicalRecord jsonMedicalRecord = medicalRecordService.findMedicalRecord(codeQR.getText(), queue.getRecordReferenceId());
        model.addAttribute("jsonMedicalRecord", jsonMedicalRecord);

        return uploadMedicalDocumentPage;
    }
}
