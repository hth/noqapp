package com.noqapp.view.controller.emp.medical;

import com.noqapp.domain.site.QueueUser;
import com.noqapp.medical.service.MedicalMasterDataService;
import com.noqapp.view.form.emp.medical.EmpMedicalLandingForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * hitender
 * 4/7/18 7:48 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/emp/medical/landing")
public class EmpMedicalLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(PathologyController.class);

    private String empMedicalLanding;

    private MedicalMasterDataService medicalMasterDataService;

    public EmpMedicalLandingController(
            @Value("${empMedicalLanding:/emp/medical/landing}")
            String empMedicalLanding,

            MedicalMasterDataService medicalMasterDataService
    ) {
        this.empMedicalLanding = empMedicalLanding;
        this.medicalMasterDataService = medicalMasterDataService;
    }

    @GetMapping
    public String empLanding(
            @ModelAttribute("empMedicalLandingForm")
            EmpMedicalLandingForm empMedicalLandingForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Employee landed qid={}", queueUser.getQueueUserId());

        empMedicalLandingForm.setCountPathology(medicalMasterDataService.countPathology());
        empMedicalLandingForm.setCountPharmacy(medicalMasterDataService.countPharmacy());
        empMedicalLandingForm.setCountPhysical(medicalMasterDataService.countPhysical());
        empMedicalLandingForm.setCountRadiology(medicalMasterDataService.countRadiology());

        return empMedicalLanding;
    }
}
