package com.noqapp.view.controller.business.customTextToSpeech;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.CustomTextToSpeechEntity;
import com.noqapp.domain.json.tts.TextToSpeechTemplate;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.CustomTextToSpeechService;
import com.noqapp.service.TextToSpeechService;
import com.noqapp.view.form.business.CustomTextToSpeechForm;
import com.noqapp.view.validator.CustomTextToSpeechValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: hitender
 * Date: 12/13/19 8:10 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/customTextToSpeech")
public class CustomTextToSpeechController {
    private static final Logger LOG = LoggerFactory.getLogger(CustomTextToSpeechController.class);

    private String nextPage;

    private CustomTextToSpeechValidator customTextToSpeechValidator;
    private CustomTextToSpeechService customTextToSpeechService;
    private BusinessUserService businessUserService;
    private TextToSpeechService textToSpeechService;

    @Autowired
    public CustomTextToSpeechController(
        @Value("${nextPage:/business/customTextToSpeech/landing}")
        String nextPage,

        CustomTextToSpeechValidator customTextToSpeechValidator,
        CustomTextToSpeechService customTextToSpeechService,
        BusinessUserService businessUserService,
        TextToSpeechService textToSpeechService
    ) {
        this.nextPage = nextPage;

        this.customTextToSpeechValidator = customTextToSpeechValidator;
        this.customTextToSpeechService = customTextToSpeechService;
        this.businessUserService = businessUserService;
        this.textToSpeechService = textToSpeechService;
    }

    /**
     * Gymnastic for PRG.
     */
    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("customTextToSpeechForm")
        CustomTextToSpeechForm customTextToSpeechForm,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Landed on survey page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.customTextToSpeechForm", model.asMap().get("result"));
        }

        CustomTextToSpeechEntity customTextToSpeech = customTextToSpeechService.findByBizNameId(businessUser.getBizName().getId());
        customTextToSpeechForm
            .setCustomTextToSpeech(customTextToSpeech)
            .setSupportedSpeechLocaleMap(CommonUtil.localeToLanguage(textToSpeechService.supportedSpeechLocale()));
        return nextPage;
    }

    @PostMapping(value = "/landing", params = {"add-announcement"}, produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("customTextToSpeechForm")
        CustomTextToSpeechForm customTextToSpeechForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Landed on survey page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        CustomTextToSpeechEntity customTextToSpeech = customTextToSpeechService.findByBizNameId(businessUser.getBizName().getId());
        customTextToSpeechForm.setCustomTextToSpeech(customTextToSpeech);

        customTextToSpeechValidator.validate(customTextToSpeechForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:/business/customTextToSpeech/landing.htm";
        }

        if (customTextToSpeech == null) {
            Map<String, Set<TextToSpeechTemplate>> textToSpeechTemplates = new HashMap<>() {{
                put(customTextToSpeechForm.getTextToSpeechType().name(), new LinkedHashSet<>() {{
                    add(new TextToSpeechTemplate()
                        .setTemplate(customTextToSpeechForm.getTemplate())
                        .setLanguageTag(customTextToSpeechForm.getLanguageTag()));
                }});
            }};

            customTextToSpeech = new CustomTextToSpeechEntity()
                .setBizNameId(businessUser.getBizName().getId())
                .setTextToSpeechTemplates(textToSpeechTemplates);
        } else {
            Map<String, Set<TextToSpeechTemplate>> textToSpeechTemplates = customTextToSpeech.getTextToSpeechTemplates();
            Set<TextToSpeechTemplate> a = textToSpeechTemplates.get(customTextToSpeechForm.getTextToSpeechType().name());

            TextToSpeechTemplate textToSpeechTemplate = new TextToSpeechTemplate()
                .setTemplate(customTextToSpeechForm.getTemplate())
                .setLanguageTag(customTextToSpeechForm.getLanguageTag());
            if (!a.isEmpty()) {
                a.remove(textToSpeechTemplate);
            }
            a.add(textToSpeechTemplate);
        }
        customTextToSpeechService.save(customTextToSpeech);

        customTextToSpeechForm
            .setTextToSpeechType(null)
            .setLanguageTag(null)
            .setTemplate(null);
        redirectAttrs.addFlashAttribute("customTextToSpeechForm", customTextToSpeechForm);
        return "redirect:" + "/business/customTextToSpeech/landing.htm";
    }

    /** For cancelling creating announcement image. */
    @PostMapping (value = "/landing", params = {"cancel-announcement"})
    public String landing() {
        return "redirect:/business/landing.htm";
    }

    @PostMapping(value = "/action", params = {"action-announcement"}, produces = "text/html;charset=UTF-8")
    public String action(
        @ModelAttribute("customTextToSpeechForm")
        CustomTextToSpeechForm customTextToSpeechForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Landed on survey page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        CustomTextToSpeechEntity customTextToSpeech = customTextToSpeechService.findByBizNameId(businessUser.getBizName().getId());
        switch (customTextToSpeechForm.getActionType()) {
            case REMOVE:
                Set<TextToSpeechTemplate> textToSpeechTemplates = customTextToSpeech.getTextToSpeechTemplates().get(customTextToSpeechForm.getTextToSpeechType().name());
                textToSpeechTemplates.remove(new TextToSpeechTemplate().setLanguageTag(customTextToSpeechForm.getLanguageTag()));

                if (textToSpeechTemplates.size() == 0) {
                    customTextToSpeech.setTextToSpeechTemplates(null);
                    customTextToSpeechService.remove(customTextToSpeech);
                } else {
                    customTextToSpeechService.save(customTextToSpeech);
                }
                break;
            default:
                LOG.error("Reached unreachable condition {}", customTextToSpeechForm.getActionType());
                throw new UnsupportedOperationException("Reached Unsupported Condition");
        }
        customTextToSpeechForm
            .setTextToSpeechType(null)
            .setLanguageTag(null)
            .setTemplate(null);
        redirectAttrs.addFlashAttribute("customTextToSpeechForm", customTextToSpeechForm);
        return "redirect:" + "/business/customTextToSpeech/landing.htm";
    }
}
