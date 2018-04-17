package com.noqapp.view.controller.emp.medical;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.medical.domain.PhysicalEntity;
import com.noqapp.medical.service.MedicalMasterDataService;
import com.noqapp.view.form.emp.medical.PhysicalForm;
import com.noqapp.view.validator.medical.PhysicalValidator;
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
@RequestMapping(value = "/emp/medical/physical")
public class PhysicalController {
    private static final Logger LOG = LoggerFactory.getLogger(PhysicalController.class);

    private String empLanding;

    private MedicalMasterDataService medicalMasterDataService;
    private PhysicalValidator physicalValidator;

    public PhysicalController(
            @Value("${empLanding:/emp/medical/physical}")
            String empLanding,

            MedicalMasterDataService medicalMasterDataService,
            PhysicalValidator physicalValidator
    ) {
        this.empLanding = empLanding;
        this.medicalMasterDataService = medicalMasterDataService;
        this.physicalValidator = physicalValidator;
    }

    @GetMapping
    public String empLanding(
            @ModelAttribute("physicalForm")
            PhysicalForm physicalForm,

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
        LOG.info("Landed on physical page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.physicalForm", model.asMap().get("result"));
            physicalForm.setId((ScrubbedInput) model.asMap().get("id"));
        } else {
            redirectAttrs.addFlashAttribute("physicalForm", physicalForm);
        }

        physicalForm.setPhysicals(medicalMasterDataService.findAllPhysicals());
//        physicalForm
//                .setName(physicalForm.getName())
//                .setDescription(physicalForm.getDescription())
//                .setNormalRange(physicalForm.getNormalRange());
        return empLanding;
    }

    /** Add new product. */
    @PostMapping(value = "/add", params = {"add"})
    public String add(
            @ModelAttribute ("physicalForm")
            PhysicalForm physicalForm,

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
        LOG.info("Adding physical qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        physicalValidator.validate(physicalForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + "/emp/medical/physical.htm";
        }

        PhysicalEntity physical = new PhysicalEntity()
                .setName(physicalForm.getName().getText())
                .setDescription(physicalForm.getDescription().getText())
                .setNormalRange(physicalForm.getNormalRangeAsString());
        medicalMasterDataService.savePhysical(physical);
        return "redirect:" + "/emp/medical/physical.htm";
    }

    /** On cancelling addition of new product. */
    @PostMapping(value = "/add", params = {"cancel_Add"})
    public String cancelAdd() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel adding new physical test qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:/emp/medical/landing.htm";
    }
}
