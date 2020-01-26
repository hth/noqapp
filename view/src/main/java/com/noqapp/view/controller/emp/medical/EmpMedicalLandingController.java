package com.noqapp.view.controller.emp.medical;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.view.form.emp.medical.EmpMedicalLandingForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * To support medical technician. For now nothings is supported.
 * hitender
 * 4/7/18 7:48 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/emp/medical/landing")
public class EmpMedicalLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(EmpMedicalLandingController.class);

    private String empMedicalLanding;

    public EmpMedicalLandingController(
        @Value("${empMedicalLanding:/emp/medical/landing}")
        String empMedicalLanding
    ) {
        this.empMedicalLanding = empMedicalLanding;
    }

    @GetMapping
    public String empLanding(
        @ModelAttribute("empMedicalLandingForm")
        EmpMedicalLandingForm empMedicalLandingForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (queueUser.getUserLevel() != UserLevelEnum.MEDICAL_TECHNICIAN) {
            LOG.warn("Could not find qid={} having access as medical user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on medical page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        return empMedicalLanding;
    }
}
