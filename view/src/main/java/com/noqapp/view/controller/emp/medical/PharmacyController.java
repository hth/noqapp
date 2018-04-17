package com.noqapp.view.controller.emp.medical;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.medical.domain.PharmacyEntity;
import com.noqapp.medical.service.MedicalMasterDataService;
import com.noqapp.view.form.emp.medical.PharmacyForm;
import com.noqapp.view.validator.medical.PharmacyValidator;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * hitender
 * 4/7/18 7:10 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/emp/medical/pharmacy")
public class PharmacyController {
    private static final Logger LOG = LoggerFactory.getLogger(PharmacyController.class);

    private String empLanding;

    private MedicalMasterDataService medicalMasterDataService;
    private PharmacyValidator pharmacyValidator;

    public PharmacyController(
            @Value("${empLanding:/emp/medical/pharmacy}")
            String empLanding,

            MedicalMasterDataService medicalMasterDataService,
            PharmacyValidator pharmacyValidator
    ) {
        this.empLanding = empLanding;
        this.medicalMasterDataService = medicalMasterDataService;
        this.pharmacyValidator = pharmacyValidator;
    }

    @GetMapping
    public String empLanding(
            @ModelAttribute("pharmacyForm")
            PharmacyForm pharmacyForm,

            Model model,
            RedirectAttributes redirectAttrs,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (queueUser.getUserLevel() == UserLevelEnum.MEDICAL_TECHNICIAN) {
            LOG.warn("Could not find qid={} having access as medical user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on pharmacy page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.pharmacyForm", model.asMap().get("result"));
            pharmacyForm.setId((ScrubbedInput) model.asMap().get("id"));
        } else {
            redirectAttrs.addFlashAttribute("pharmacyForm", pharmacyForm);
        }

        pharmacyForm.setPharmacies(medicalMasterDataService.findAllPharmacies());
//        pharmacyForm
//                .setName(pharmacyForm.getName())
//                .setPharmacyMeasurementUnit(pharmacyForm.getPharmacyMeasurementUnit())
//                .setValue(pharmacyForm.getValue())
//                .setCompanyName(pharmacyForm.getCompanyName())
//                .setReferStaticLink(pharmacyForm.getReferStaticLink());
        return empLanding;
    }

    /** Add new product. */
    @PostMapping(value = "/add", params = {"add"})
    public String add(
            @ModelAttribute ("pharmacyForm")
            PharmacyForm pharmacyForm,

            BindingResult result,
            RedirectAttributes redirectAttrs,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (queueUser.getUserLevel() == UserLevelEnum.MEDICAL_TECHNICIAN) {
            LOG.warn("Could not find qid={} having access as medical user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Adding pharmacy qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        pharmacyValidator.validate(pharmacyForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + "/emp/medical/pharmacy.htm";
        }

        PharmacyEntity pharmacy = new PharmacyEntity()
                .setName(pharmacyForm.getName().getText())
                .setValue(pharmacyForm.getValue())
                .setPharmacyMeasurementUnit(pharmacyForm.getPharmacyMeasurementUnit())
                .setCompanyName(pharmacyForm.getCompanyName().getText())
                .setReferStaticLink(pharmacyForm.getReferStaticLink().getText());
        medicalMasterDataService.savePharmacy(pharmacy);
        return "redirect:" + "/emp/medical/pharmacy.htm";
    }

    /** On cancelling addition of new product. */
    @PostMapping(value = "/add", params = {"cancel_Add"})
    public String cancelAdd() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel adding new pharmacy test qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:/emp/medical/landing.htm";
    }
}
