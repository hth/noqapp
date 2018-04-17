package com.noqapp.view.controller.emp.medical;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.medical.domain.PathologyEntity;
import com.noqapp.medical.service.MedicalMasterDataService;
import com.noqapp.view.form.emp.medical.PathologyForm;
import com.noqapp.view.validator.medical.PathologyValidator;
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
 * 4/7/18 7:11 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/emp/medical/pathology")
public class PathologyController {
    private static final Logger LOG = LoggerFactory.getLogger(PathologyController.class);

    private String empLanding;

    private MedicalMasterDataService medicalMasterDataService;
    private PathologyValidator pathologyValidator;

    public PathologyController(
            @Value("${empLanding:/emp/medical/pathology}")
            String empLanding,

            MedicalMasterDataService medicalMasterDataService,
            PathologyValidator pathologyValidator
    ) {
        this.empLanding = empLanding;

        this.medicalMasterDataService = medicalMasterDataService;
        this.pathologyValidator = pathologyValidator;
    }

    @GetMapping
    public String empLanding(
            @ModelAttribute("pathologyForm")
            PathologyForm pathologyForm,

            Model model,
            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (queueUser.getUserLevel() == UserLevelEnum.MEDICAL_TECHNICIAN) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on pathology page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.pathologyForm", model.asMap().get("result"));
            pathologyForm.setId((ScrubbedInput) model.asMap().get("id"));
        } else {
            redirectAttrs.addFlashAttribute("pathologyForm", pathologyForm);
        }

        //Add category support
        //Map<String, String> categories = storeCategoryService.getStoreCategoriesAsMap(storeId.getText());
        pathologyForm.setPathologies(medicalMasterDataService.findAllPathologies());
        pathologyForm
                .setName(pathologyForm.getName())
                .setCategory(pathologyForm.getCategory())
                .setDescription(pathologyForm.getDescription());
        return empLanding;
    }

    /** Add new product. */
    @PostMapping(value = "/add", params = {"add"})
    public String add(
            @ModelAttribute ("pathologyForm")
            PathologyForm pathologyForm,

            BindingResult result,
            RedirectAttributes redirectAttrs,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (queueUser.getUserLevel() == UserLevelEnum.MEDICAL_TECHNICIAN) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Adding pathology qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        pathologyValidator.validate(pathologyForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + "/emp/medical/pathology.htm";
        }

        PathologyEntity pathology = new PathologyEntity()
                .setCategory(pathologyForm.getCategory().getText())
                .setDescription(pathologyForm.getDescription().getText())
                .setName(pathologyForm.getName().getText());
        medicalMasterDataService.savePathology(pathology);
        return "redirect:" + "/emp/medical/pathology.htm";
    }

    /** On cancelling addition of new product. */
    @PostMapping (value = "/add", params = {"cancel_Add"})
    public String cancelAdd() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel adding new pathology test qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:/emp/medical/landing.htm";
    }
}
