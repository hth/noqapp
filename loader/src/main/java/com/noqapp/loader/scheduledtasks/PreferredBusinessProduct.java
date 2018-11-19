package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.service.MedicalFileService;
import com.noqapp.service.FileService;
import com.noqapp.service.StatsCronService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * hitender
 * 8/22/18 7:03 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class PreferredBusinessProduct {
    private static final Logger LOG = LoggerFactory.getLogger(PreferredBusinessProduct.class);

    private FileService fileService;
    private MedicalFileService medicalFileService;
    private StatsCronService statsCronService;

    private String makePreferredBusinessFiles;

    @Autowired
    public PreferredBusinessProduct(
        @Value("${makePreferredBusinessFiles:ON}")
        String makePreferredBusinessFiles,

        FileService fileService,
        MedicalFileService medicalFileService,
        StatsCronService statsCronService
    ) {
        this.makePreferredBusinessFiles = makePreferredBusinessFiles;

        this.fileService = fileService;
        this.medicalFileService = medicalFileService;
        this.statsCronService = statsCronService;
    }

    /** Create zip file of all the products for a business store. */
    @Scheduled(cron = "${loader.PreferredBusinessProduct.makeTarFile}")
    public void makeTarFile() {
        StatsCronEntity statsCron = new StatsCronEntity(
            PreferredBusinessProduct.class.getName(),
            "makeTarFile",
            makePreferredBusinessFiles);

        if ("OFF".equalsIgnoreCase(makePreferredBusinessFiles)) {
            return;
        }

        int success = 0, failure = 0;
        try {
            for (BusinessTypeEnum businessType : BusinessTypeEnum.asList()) {
                switch (businessType) {
                    case PH:
                    case PT:
                    case PY:
                    case RA:
                        fileService.findAllBizStoreWithBusinessType(businessType);
                        success ++;
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LOG.error("Error makeTarFile for preferred business reason={}", e.getLocalizedMessage(), e);
            failure ++;
        } finally {
            if (0 != success || 0 != failure) {
                statsCron.addStats("success", success);
                statsCron.addStats("failure", failure);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete success={} failure={}", success, failure);
            }
        }
    }

    /** Create zip file of all the products for a business type. */
    @Scheduled(cron = "${loader.PreferredBusinessProduct.makeTarFile}")
    public void makeMasterTarFile() {
        StatsCronEntity statsCron = new StatsCronEntity(
            PreferredBusinessProduct.class.getName(),
            "makeMasterTarFile",
            makePreferredBusinessFiles);

        if ("OFF".equalsIgnoreCase(makePreferredBusinessFiles)) {
            return;
        }

        int success = 0, failure = 0;
        try {
            for (BusinessTypeEnum businessType : BusinessTypeEnum.asList()) {
                switch (businessType) {
                    case PH:
                        break;
                    case PT:
                        for (MedicalDepartmentEnum medicalDepartment : MedicalDepartmentEnum.values()) {
                            medicalFileService.createMasterFileAssociatedWithBusinessType(businessType, medicalDepartment);
                        }
                        success ++;
                        break;
                    case PY:
                        break;
                    case RA:
                        for (MedicalDepartmentEnum medicalDepartment : MedicalDepartmentEnum.values()) {
                            medicalFileService.createMasterFileAssociatedWithBusinessType(businessType, medicalDepartment);
                        }
                        success ++;
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LOG.error("Error makeTarFile for preferred business reason={}", e.getLocalizedMessage(), e);
            failure ++;
        } finally {
            if (0 != success || 0 != failure) {
                statsCron.addStats("success", success);
                statsCron.addStats("failure", failure);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete success={} failure={}", success, failure);
            }
        }
    }
}
