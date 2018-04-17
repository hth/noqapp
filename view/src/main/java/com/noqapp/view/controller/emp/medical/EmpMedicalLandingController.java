package com.noqapp.view.controller.emp.medical;

import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.UserLevelEnum;
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

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

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
            EmpMedicalLandingForm empMedicalLandingForm,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (queueUser.getUserLevel() == UserLevelEnum.MEDICAL_TECHNICIAN) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on medical page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        empMedicalLandingForm.setCountPathology(medicalMasterDataService.countPathology());
        empMedicalLandingForm.setCountPharmacy(medicalMasterDataService.countPharmacy());
        empMedicalLandingForm.setCountPhysical(medicalMasterDataService.countPhysical());
        empMedicalLandingForm.setCountRadiology(medicalMasterDataService.countRadiology());

        return empMedicalLanding;
    }
}
