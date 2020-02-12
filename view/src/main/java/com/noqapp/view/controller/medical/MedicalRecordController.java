package com.noqapp.view.controller.medical;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.medical.domain.MedicalMedicineEntity;
import com.noqapp.medical.domain.MedicalPhysicalEntity;
import com.noqapp.medical.domain.MedicalRecordEntity;
import com.noqapp.medical.domain.UserMedicalProfileEntity;
import com.noqapp.medical.form.MedicalRecordForm;
import com.noqapp.medical.form.UserMedicalProfileForm;
import com.noqapp.medical.service.MedicalRecordService;
import com.noqapp.medical.service.UserMedicalProfileService;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.service.AccountService;
import com.noqapp.service.QueueService;
import com.noqapp.service.TokenQueueService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 3/5/18 1:22 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/medical/record")
public class MedicalRecordController {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalRecordController.class);

    private String nextPage;

    private RegisteredDeviceManager registeredDeviceManager;
    private QueueService queueService;
    private TokenQueueService tokenQueueService;
    private AccountService accountService;
    private MedicalRecordService medicalRecordService;
    private UserMedicalProfileService userMedicalProfileService;
    private ApiHealthService apiHealthService;

    @Autowired
    public MedicalRecordController(
        @Value("${nextPage:/medical/caseHistory}")
        String nextPage,

        RegisteredDeviceManager registeredDeviceManager,
        QueueService queueService,
        TokenQueueService tokenQueueService,
        AccountService accountService,
        MedicalRecordService medicalRecordService,
        UserMedicalProfileService userMedicalProfileService,
        ApiHealthService apiHealthService
    ) {
        this.nextPage = nextPage;

        this.registeredDeviceManager = registeredDeviceManager;
        this.queueService = queueService;
        this.tokenQueueService = tokenQueueService;
        this.accountService = accountService;
        this.medicalRecordService = medicalRecordService;
        this.userMedicalProfileService = userMedicalProfileService;
        this.apiHealthService = apiHealthService;
    }

    @GetMapping(value = "/{codeQR}/{recordReferenceId}")
    public ModelAndView createRecord(
            @PathVariable("codeQR")
            ScrubbedInput codeQR,

            @PathVariable("recordReferenceId")
            ScrubbedInput recordReferenceId,

            HttpServletResponse response
    ) {
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        ModelAndView modelAndView = new ModelAndView(nextPage);
        try {
            String identifier = new String(Base64.getDecoder().decode(recordReferenceId.getText()), StandardCharsets.ISO_8859_1);
            String[] recordReference = identifier.split("#");

            int token = Integer.parseInt(recordReference[0]);
            String qid = recordReference[1];
            String recordOwner = recordReference[2];

            MedicalRecordForm medicalRecordForm = new MedicalRecordForm(recordOwner);
            QueueEntity queue = tokenQueueService.findOne(codeQR.getText(), token);
            if (null == queue.getServiceBeginTime()) {
                queueService.updateServiceBeginTime(queue.getId());
            }

            if (StringUtils.isNotBlank(queue.getQueueUserId()) && qid.equalsIgnoreCase(queue.getQueueUserId())) {
                UserProfileEntity userProfile = accountService.findProfileByQueueUserId(recordOwner);
                medicalRecordForm
                        .setToken(token)
                        .setBusinessType(queue.getBusinessType())
                        .setCodeQR(new ScrubbedInput(queue.getCodeQR()))
                        .setPatientName(userProfile.getName())
                        .setGender(userProfile.getGender())
                        .setAge(userProfile.getAgeAsString());

                if (StringUtils.isNotBlank(userProfile.getGuardianPhone())) {
                    UserProfileEntity guardianProfile = accountService.checkUserExistsByPhone(userProfile.getGuardianPhone());
                    medicalRecordForm.setGuardianName(guardianProfile.getName())
                            .setGuardianPhone(guardianProfile.getPhone());
                }
            } else {
                //Perform Account Registry
            }

            List<MedicalRecordEntity> historicalMedicalRecords = medicalRecordService.historicalRecords(recordOwner);
            List<MedicalRecordForm> historicalMedicalRecordForms = new LinkedList<>();
            for (MedicalRecordEntity medicalRecord : historicalMedicalRecords) {
                List<MedicalPhysicalEntity> medicalPhysicals = new ArrayList<>();
                if (null != medicalRecord.getMedicalPhysicalId()) {
                    medicalPhysicals = medicalRecordService.findByQid(medicalRecord.getQueueUserId());
                }

                MedicalRecordForm historicalMedicalRecordForm = new MedicalRecordForm(medicalRecord.getQueueUserId());
                historicalMedicalRecordForm
                        .populatePhysicalHistoricalForm(medicalPhysicals)
                        .setBusinessType(medicalRecord.getBusinessType())
                        .setChiefComplain(medicalRecord.getChiefComplain())
                        .setClinicalFinding(medicalRecord.getClinicalFinding())
                        .setProvisionalDifferentialDiagnosis(medicalRecord.getProvisionalDifferentialDiagnosis())
                        .setMedicalMedication(medicalRecordService.findByMedicationId(medicalRecord.getMedicalMedicationId()));

                List<MedicalMedicineEntity> medicalMedicines = medicalRecordService.findByMedicationRefId(medicalRecord.getMedicalMedicationId());
                historicalMedicalRecordForm.setMedicalMedicines(medicalMedicines);
                historicalMedicalRecordForms.add(historicalMedicalRecordForm);
            }

            UserMedicalProfileEntity userMedicalProfile = userMedicalProfileService.findOne(recordOwner);
            UserMedicalProfileForm userMedicalProfileForm = new UserMedicalProfileForm()
                .setBloodType(userMedicalProfile.getBloodType())
                .setOccupation(userMedicalProfile.getOccupation())
                .setPastHistory(userMedicalProfile.getPastHistory())
                .setFamilyHistory(userMedicalProfile.getFamilyHistory())
                .setKnownAllergies(userMedicalProfile.getKnownAllergies())
                .setMedicineAllergies(userMedicalProfile.getMedicineAllergies());

            modelAndView.addObject("userMedicalProfileForm", userMedicalProfileForm);
            modelAndView.addObject("medicalRecordForm", medicalRecordForm);
            modelAndView.addObject("historicalMedicalRecordForms", historicalMedicalRecordForms);
            return modelAndView;
        } catch (Exception e) {
            LOG.error("Failed to get records reason={}", e.getLocalizedMessage(), e);
            methodStatusSuccess = false;
            return modelAndView;
        } finally {
            apiHealthService.insert(
                    "/{codeQR}/{recordReferenceId}",
                    "createRecord",
                    MedicalRecordController.class.getName(),
                    Duration.between(start, Instant.now()),
                    methodStatusSuccess ? HealthStatusEnum.G : HealthStatusEnum.F);
        }
    }

    @PostMapping(value = "/add")
    public String addRecord(
            @ModelAttribute("medicalRecordForm")
            MedicalRecordForm medicalRecordForm
    ) {
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        try {
            QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(queueUser.getQueueUserId());

            /* Note: Since being served by web, we will assume its being served by one of the QID user's device. */
            String deviceId;
            if (registeredDevice == null) {
                /* Note: Should complain if no registered device found under the QID. */
                //throw new RuntimeException("No registered device found");
                deviceId = CommonUtil.appendRandomToDeviceId(queueUser.getQueueUserId() + "-" + TokenServiceEnum.W.getName());
            } else {
                deviceId = registeredDevice.getDeviceId();
            }

            LOG.info("MedicalRecordForm={}", medicalRecordForm);
            //Validate if the person has joined the queue
            medicalRecordService.addMedicalRecord(medicalRecordForm, queueUser.getQueueUserId(), medicalRecordForm.getCodeQR().getText());
            queueService.updateAndGetNextInQueue(
                    medicalRecordForm.getCodeQR().getText(),
                    medicalRecordForm.getToken(),
                    QueueUserStateEnum.S,
                    "",
                    deviceId,
                    TokenServiceEnum.W);
            return "redirect:/business/store/sup/" + medicalRecordForm.getCodeQR() + ".htm";
        } catch (Exception e) {
            LOG.error("Failed to get records reason={}", e.getLocalizedMessage(), e);
            methodStatusSuccess = false;
            return "redirect:" + "/medical/record/add.htm";
        } finally {
            apiHealthService.insert(
                    "/add",
                    "addRecord",
                    MedicalRecordController.class.getName(),
                    Duration.between(start, Instant.now()),
                    methodStatusSuccess ? HealthStatusEnum.G : HealthStatusEnum.F);
        }
    }
}
