package com.noqapp.view.controller.business.documentation;

import static com.noqapp.view.controller.access.LandingController.SUCCESS;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedPerson;
import com.noqapp.domain.json.medical.JsonUserMedicalProfile;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.medical.domain.MedicalRecordEntity;
import com.noqapp.medical.domain.json.JsonMedicalRecord;
import com.noqapp.medical.service.MedicalFileService;
import com.noqapp.medical.service.MedicalRecordService;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.service.QueueService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.controller.access.UserProfileController;
import com.noqapp.view.form.business.MedicalDocumentUploadForm;
import com.noqapp.view.form.business.MedicalDocumentUploadListForm;

import com.google.gson.JsonObject;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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

    private int limitedToDays;

    private String nextPage;
    private String uploadMedicalDocumentPage;
    private String historicalPage;

    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;
    private QueueService queueService;
    private BizService bizService;
    private MedicalRecordService medicalRecordService;
    private TokenQueueService tokenQueueService;
    private MedicalFileService medicalFileService;
    private ProfessionalProfileService professionalProfileService;
    private AccountService accountService;
    private ApiHealthService apiHealthService;

    @Autowired
    public MedicalDocumentationController(
        @Value("${limitedToDays:7}")
        int limitedToDays,

        @Value("${nextPage:/business/documentation/medical/landing}")
        String nextPage,

        @Value("${uploadMedicalDocumentPage:/business/documentation/medical/uploadMedicalDocument}")
        String uploadMedicalDocumentPage,

        @Value("${historicalPage:/business/documentation/medical/historical}")
        String historicalPage,

        BusinessUserService businessUserService,
        BusinessUserStoreService businessUserStoreService,
        QueueService queueService,
        BizService bizService,
        MedicalRecordService medicalRecordService,
        TokenQueueService tokenQueueService,
        MedicalFileService medicalFileService,
        ProfessionalProfileService professionalProfileService,
        AccountService accountService,
        ApiHealthService apiHealthService
    ) {
        this.limitedToDays = limitedToDays;

        this.nextPage = nextPage;
        this.uploadMedicalDocumentPage = uploadMedicalDocumentPage;
        this.historicalPage = historicalPage;

        this.businessUserService = businessUserService;
        this.businessUserStoreService = businessUserStoreService;
        this.queueService = queueService;
        this.bizService = bizService;
        this.medicalRecordService = medicalRecordService;
        this.tokenQueueService = tokenQueueService;
        this.medicalFileService = medicalFileService;
        this.professionalProfileService = professionalProfileService;
        this.accountService = accountService;
        this.apiHealthService = apiHealthService;
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
        LOG.info("Landed on upload medical landing page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        medicalDocumentUploadListForm.setBusinessName(businessUser.getBizName().getBusinessName());
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreService.findAllStoreQueueAssociated(queueUser.getQueueUserId());
        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            String codeQR = businessUserStore.getCodeQR();
            JsonQueuePersonList jsonQueuePersonList = queueService.findAllClient(codeQR);
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
            StoreHourEntity storeHour = bizService.getStoreHours(codeQR, bizStore);
            bizStore.setStoreHours(new ArrayList<StoreHourEntity>() {{
                add(storeHour);
            }});
            medicalDocumentUploadListForm.addMedicalDocumentUploadForms(
                new MedicalDocumentUploadForm()
                    .setBizStore(bizStore)
                    .setJsonQueuePersonList(jsonQueuePersonList));
        }
        return nextPage;
    }

    @GetMapping(value = "/{encryptedId}/upload/{codeQR}", produces = "text/html;charset=UTF-8")
    public String uploadDocument(
        @PathVariable("encryptedId")
        ScrubbedInput encryptedId,

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

        if (StringUtils.isBlank(codeQR.getText())) {
            LOG.warn("Not a valid codeQR={} qid={}", codeQR.getText(), queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        } else if (!businessUserStoreService.hasAccess(queueUser.getQueueUserId(), codeQR.getText())) {
            LOG.info("Your are not authorized to access medical record mail={}", queueUser.getUsername());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on upload document page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        String identifier = new String(Base64.getDecoder().decode(encryptedId.getText()), StandardCharsets.ISO_8859_1);
        String[] recordReference = identifier.split("#");

        int token = Integer.parseInt(recordReference[0]);
        String qid = recordReference[1];
        String recordOwner = recordReference[2];

        QueueEntity queue = tokenQueueService.findOne(codeQR.getText(), token);
        createReferenceToMedicalRecord(codeQR.getText(), model, queue);
        JsonMedicalRecord jsonMedicalRecord = (JsonMedicalRecord) model.getAttribute("jsonMedicalRecord");
        if (null != jsonMedicalRecord) {
            jsonMedicalRecord.setCreateDate(DateUtil.dateToISO_8601(queue.getServiceEndTime()));
        }

        return uploadMedicalDocumentPage;
    }

    @PostMapping(value = "/{recordReferenceId}/upload/{codeQR}")
    @ResponseBody
    public String uploadDocument(
        @PathVariable("recordReferenceId")
        ScrubbedInput recordReferenceId,

        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        HttpServletRequest httpServletRequest,
        HttpServletResponse response
    ) throws IOException {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (StringUtils.isBlank(codeQR.getText())) {
            LOG.warn("Not a valid codeQR={} qid={}", codeQR.getText(), queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        } else if (!businessUserStoreService.hasAccess(queueUser.getQueueUserId(), codeQR.getText())) {
            LOG.info("Your are not authorized to access medical record mail={}", queueUser.getUsername());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Uploading medical image qid={} codeQR={}", queueUser.getQueueUserId(), codeQR.getText());
        /* Above condition to make sure users with right roles and access gets access. */

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SUCCESS, false);

        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if (isMultipart) {
            MultipartHttpServletRequest multipartHttpRequest = WebUtils.getNativeRequest(httpServletRequest, MultipartHttpServletRequest.class);
            final List<MultipartFile> files = UserProfileController.getMultipartFiles(multipartHttpRequest, "qqfile");

            if (!files.isEmpty()) {
                MultipartFile multipartFile = files.iterator().next();

                try {
                    String filename = medicalFileService.processMedicalImageWithoutWritingToRecord(recordReferenceId.getText(), multipartFile);
                    LOG.info("Added image to medical record {} {}", recordReferenceId.getText(), filename);
                    medicalRecordService.addImage(recordReferenceId.getText(), filename);
                    jsonObject.addProperty(SUCCESS, true);
                } catch (Exception e) {
                    LOG.error("Failed store image upload reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/{recordReferenceId}/upload/{codeQR}",
                        "uploadDocument",
                        MedicalDocumentationController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }
            }
        }

        return jsonObject.toString();
    }

    @GetMapping(value = "/historicalLanding/{codeQR}", produces = "text/html;charset=UTF-8")
    public String loadHistorical(
        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        @ModelAttribute("medicalDocumentUploadListForm")
        MedicalDocumentUploadListForm medicalDocumentUploadListForm,

        HttpServletRequest httpServletRequest,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }

        if (StringUtils.isBlank(codeQR.getText())) {
            LOG.warn("Not a valid codeQR={} qid={}", codeQR.getText(), queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        } else if (!businessUserStoreService.hasAccess(queueUser.getQueueUserId(), codeQR.getText())) {
            LOG.info("Your are not authorized to access medical record mail={}", queueUser.getUsername());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on historical medical page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        JsonQueuePersonList jsonQueuePersonList = queueService.findAllRegisteredClientHistorical(codeQR.getText(), DateUtil.minusDays(limitedToDays), DateUtil.plusDays(1));
        List<JsonQueuedPerson> queuedPeople = jsonQueuePersonList.getQueuedPeople();

        Map<String, List<JsonQueuedPerson>> jsonQueuedPersonMap = new HashMap<>();
        for (JsonQueuedPerson jsonQueuedPerson : queuedPeople) {
            try {
                Date date = DateUtil.SDF_YYYY_MM_DD.parse(jsonQueuedPerson.getCreated());
                String dateToString = DateUtil.dateToString(date);
                if (jsonQueuedPersonMap.containsKey(dateToString)) {
                    List<JsonQueuedPerson> jsonQueuedPersons = jsonQueuedPersonMap.get(dateToString);
                    jsonQueuedPersons.add(jsonQueuedPerson);
                } else {
                    jsonQueuedPersonMap.put(dateToString, new ArrayList<JsonQueuedPerson>() {{
                        add(jsonQueuedPerson);
                    }});
                }
            } catch (ParseException e) {
                LOG.error("Failed parsing date created={} {}", jsonQueuedPerson.getCreated(), e.getLocalizedMessage(), e);
            }
        }
        medicalDocumentUploadListForm.setJsonQueuedPersonMap(jsonQueuedPersonMap);
        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR.getText());

        MedicalDocumentUploadForm medicalDocumentUploadForm = new MedicalDocumentUploadForm();
        medicalDocumentUploadForm
            .setBizStore(bizStore)
            .setJsonQueuePersonList(jsonQueuePersonList);
        medicalDocumentUploadListForm.addMedicalDocumentUploadForms(medicalDocumentUploadForm);
        medicalDocumentUploadListForm.setBusinessName(bizStore.getBizName().getBusinessName());
        return historicalPage;
    }

    @GetMapping(value = "/{recordReferenceId}/uploadHistorical/{codeQR}", produces = "text/html;charset=UTF-8")
    public String uploadHistoricalDocument(
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

        if (StringUtils.isBlank(codeQR.getText())) {
            LOG.warn("Not a valid codeQR={} qid={}", codeQR.getText(), queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        } else if (!businessUserStoreService.hasAccess(queueUser.getQueueUserId(), codeQR.getText())) {
            LOG.info("Your are not authorized to access medical record mail={}", queueUser.getUsername());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Uploading historical medical image page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        MedicalRecordEntity medicalRecord = medicalRecordService.findByMedicalRecordId(recordReferenceId.getText());
        if (null != medicalRecord && DateUtil.getDaysBetween(medicalRecord.getCreated(), DateUtil.nowDate()) > limitedToDays) {
            /* To prevent alteration in records after the record has been created. */
            LOG.info("Since medical record already exists. No new upload can be performed");
            return uploadMedicalDocumentPage;
        }

        QueueEntity queue = queueService.findOneHistoricalByRecordReferenceId(codeQR.getText(), recordReferenceId.getText());
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUser.getQueueUserId());
        queue
            .setCustomerName(userProfile.getName())
            .setCustomerPhone(StringUtils.isBlank(userProfile.getPhone()) ? userProfile.getGuardianPhone() : userProfile.getPhone());
        createReferenceToMedicalRecord(codeQR.getText(), model, queue);

        if (null == medicalRecord) {
            medicalRecordService.updateCreateDate(recordReferenceId.getText(), queue.getServiceEndTime());
            JsonMedicalRecord jsonMedicalRecord = (JsonMedicalRecord) model.getAttribute("jsonMedicalRecord");
            if (null != jsonMedicalRecord) {
                jsonMedicalRecord.setCreateDate(DateUtil.dateToISO_8601(queue.getServiceEndTime()));
            }
        }
        return uploadMedicalDocumentPage;
    }

    /** Creates medical record when does not exists or populates medical record. */
    private void createReferenceToMedicalRecord(String codeQR, Model model, QueueEntity queue) {
        model.addAttribute("queue", queue);

        JsonQueuedPerson jsonQueuedPerson = queueService.getJsonQueuedPerson(queue);
        model.addAttribute("jsonQueuedPerson", jsonQueuedPerson);

        JsonMedicalRecord jsonMedicalRecord = medicalRecordService.findMedicalRecord(codeQR, queue.getRecordReferenceId(), queue);
        if (null == jsonMedicalRecord) {
            BusinessUserStoreEntity businessUserStore = businessUserStoreService.findUserManagingStoreWithCodeQRAndUserLevel(codeQR);
            ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(businessUserStore.getQueueUserId());
            jsonMedicalRecord = new JsonMedicalRecord()
                .setCodeQR(codeQR)
                .setRecordReferenceId(queue.getRecordReferenceId())
                .setQueueUserId(queue.getQueueUserId())
                .setBusinessName(queue.getDisplayName())
                .setBusinessType(queue.getBusinessType())
                .setJsonUserMedicalProfile(new JsonUserMedicalProfile())
                .setFormVersion(professionalProfile.getFormVersion());
            medicalRecordService.addMedicalRecord(jsonMedicalRecord, businessUserStore.getQueueUserId());
        }
        model.addAttribute("jsonMedicalRecord", jsonMedicalRecord);
    }
}
