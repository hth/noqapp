package com.noqapp.view.controller.emp.medical;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.medical.domain.RadiologyEntity;
import com.noqapp.medical.service.MedicalMasterDataService;
import com.noqapp.view.form.emp.medical.RadiologyForm;
import com.noqapp.view.validator.medical.RadiologyValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 4/7/18 7:11 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/emp/medical/radiology")
public class RadiologyController {
    private static final Logger LOG = LoggerFactory.getLogger(RadiologyController.class);

    private String empLanding;

    private MedicalMasterDataService medicalMasterDataService;
    private RadiologyValidator radiologyValidator;

    public RadiologyController(
            @Value("${empLanding:/emp/medical/radiology}")
            String empLanding,

            MedicalMasterDataService medicalMasterDataService,
            RadiologyValidator radiologyValidator
    ) {
        this.empLanding = empLanding;
        this.medicalMasterDataService = medicalMasterDataService;
        this.radiologyValidator = radiologyValidator;
    }

    @GetMapping
    public String empLanding(
            @ModelAttribute("radiologyForm")
            RadiologyForm radiologyForm,

            Model model,
            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (queueUser.getUserLevel() != UserLevelEnum.MEDICAL_TECHNICIAN) {
            LOG.warn("Could not find qid={} having access as medical user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on radiology page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.radiologyForm", model.asMap().get("result"));
            radiologyForm.setId((ScrubbedInput) model.asMap().get("id"));
        } else {
            redirectAttrs.addFlashAttribute("radiologyForm", radiologyForm);
        }

        radiologyForm.setRadiologies(medicalMasterDataService.findAllRadiologies());
//        radiologyForm
//                .setCategory(radiologyForm.getCategory())
//                .setName(radiologyForm.getName());
        return empLanding;
    }

    /** Add new product. */
    @PostMapping(value = "/add", params = {"add"})
    public String add(
            @ModelAttribute ("radiologyForm")
            RadiologyForm radiologyForm,

            BindingResult result,
            RedirectAttributes redirectAttrs,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (queueUser.getUserLevel() != UserLevelEnum.MEDICAL_TECHNICIAN) {
            LOG.warn("Could not find qid={} having access as medical user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Adding radiology qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        radiologyValidator.validate(radiologyForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + "/emp/medical/radiology.htm";
        }

        RadiologyEntity radiology = new RadiologyEntity()
                .setCategory(radiologyForm.getCategory().getText())
                .setName(radiologyForm.getName().getText());
        medicalMasterDataService.saveRadiology(radiology);
        return "redirect:" + "/emp/medical/radiology.htm";
    }

    /** On cancelling addition of new product. */
    @PostMapping(value = "/add", params = {"cancel_Add"})
    public String cancelAdd() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel adding new radiology test qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:/emp/medical/landing.htm";
    }
}
