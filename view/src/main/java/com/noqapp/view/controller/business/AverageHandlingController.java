package com.noqapp.view.controller.business;

import static com.noqapp.common.utils.DateUtil.MINUTES_IN_MILLISECONDS;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.helper.ExpectedHandlingTime;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.view.form.business.AverageHandlingForm;
import com.noqapp.view.validator.AverageHandlingTimeValidator;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 6/30/20 4:58 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/averageHandling")
public class AverageHandlingController {
    private static final Logger LOG = LoggerFactory.getLogger(AverageHandlingController.class);

    private String nextPage;

    private BizService bizService;
    private BusinessUserService businessUserService;
    private AverageHandlingTimeValidator averageHandlingTimeValidator;

    @Autowired
    public AverageHandlingController(
        @Value("${storeDetail:/business/averageHandling}")
        String nextPage,

        BizService bizService,
        BusinessUserService businessUserService,
        AverageHandlingTimeValidator averageHandlingTimeValidator
    ) {
        this.nextPage = nextPage;

        this.bizService = bizService;
        this.businessUserService = businessUserService;
        this.averageHandlingTimeValidator = averageHandlingTimeValidator;
    }

    @GetMapping(value = "/{storeId}", produces = "text/html;charset=UTF-8")
    public String landing(
        @PathVariable("storeId")
        ScrubbedInput storeId,

        @ModelAttribute("averageHandlingForm")
        AverageHandlingForm averageHandlingForm,

        Model model,
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        List<StoreHourEntity> storeHours = bizService.findAllStoreHours(storeId.getText());

        averageHandlingForm.setBizStoreId(storeId.getText())
            .setDisplayName(bizStore.getDisplayName())
            .setAvailableTokenCount(bizStore.getAvailableTokenCount())
            .setAverageServiceTime(bizStore.getAverageServiceTime());

        for (StoreHourEntity storeHour : storeHours) {
            long minutes = storeHour.storeOpenDurationInMinutes();
            Duration duration = Duration.ofMinutes(minutes);

            BigDecimal averageServiceTime = bizStore.getAvailableTokenCount() == 0
                ? new BigDecimal(0)
                : new BigDecimal(minutes).divide(new BigDecimal(bizStore.getAvailableTokenCount()), MathContext.DECIMAL64).setScale(2, RoundingMode.CEILING);
            ExpectedHandlingTime expectedHandlingTime = new ExpectedHandlingTime()
                .setDuration(duration)
                .setAverageServiceTime(averageServiceTime.multiply(new BigDecimal(MINUTES_IN_MILLISECONDS)).longValue())
                .setClosed(storeHour.isDayClosed());

            if (!storeHour.isDayClosed()) {
                averageHandlingForm.addAvailableDayOfWeeks(DayOfWeek.of(storeHour.getDayOfWeek()));
            }

            LOG.debug("Open hours {} {} {}", duration.toString(), storeHour.getDayOfTheWeekAsString(), averageServiceTime);
            averageHandlingForm.getOpenDurationEachDayOfWeek().put(storeHour.getDayOfTheWeekAsString(), expectedHandlingTime);
        }

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.averageHandlingForm", model.asMap().get("result"));
        } else {
            redirectAttrs.addFlashAttribute("averageHandlingForm", averageHandlingForm);
        }

        return nextPage;
    }

    @PostMapping(value = "/landing",  produces = "text/html;charset=UTF-8", params = "update-aht")
    public String updateAverageHandlingTime(
        @ModelAttribute("averageHandlingForm")
        AverageHandlingForm averageHandlingForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        averageHandlingTimeValidator.validate(averageHandlingForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:/business/averageHandling/" + averageHandlingForm.getBizStoreId() + ".htm";
        }

        BizStoreEntity bizStore = bizService.getByStoreId(averageHandlingForm.getBizStoreId());
        StoreHourEntity storeHour = bizService.findStoreHour(bizStore.getId(), averageHandlingForm.getSelectedDayOfWeek());

        long minutes = storeHour.storeOpenDurationInMinutes();
        long averageHandlingTime = new BigDecimal(minutes)
            .divide(new BigDecimal(averageHandlingForm.getAvailableTokenCount()), MathContext.DECIMAL64)
            .multiply(new BigDecimal(MINUTES_IN_MILLISECONDS)).longValue();
        LOG.debug("ExistingToken={} NewToken={} ExistingAHT={} UpdatedAHT={}",
            bizStore.getAvailableTokenCount(),
            averageHandlingForm.getAvailableTokenCount(),
            bizStore.getAverageServiceTime(),
            averageHandlingForm);

        bizService.updateStoreTokenAndHandlingTime(bizStore.getCodeQR(), averageHandlingTime, averageHandlingForm.getAvailableTokenCount());
        return "redirect:/business/landing.htm";
    }

    @PostMapping(value = "/landing",  produces = "text/html;charset=UTF-8", params = "cancel-aht")
    public String updateAverageHandlingTime() {
        LOG.info("Cancel search");
        return "redirect:/business/landing.htm";
    }
}
