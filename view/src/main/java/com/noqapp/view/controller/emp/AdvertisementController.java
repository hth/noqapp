package com.noqapp.view.controller.emp;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.AdvertisementEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.service.AdvertisementService;
import com.noqapp.view.form.business.AdvertisementForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 2019-05-16 23:19
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/emp/advertisement")
public class AdvertisementController {
    private static final Logger LOG = LoggerFactory.getLogger(AdvertisementController.class);

    private String nextPage;

    private AdvertisementService advertisementService;

    @Autowired
    public AdvertisementController(
        @Value("${nextPage:/business/advertisement/preview}")
        String nextPage,

        AdvertisementService advertisementService
    ) {
        this.nextPage = nextPage;

        this.advertisementService = advertisementService;
    }

    @GetMapping(value = "/approval/{advertisementId}/preview", produces = "text/html;charset=UTF-8")
    public String preview(
        @PathVariable("advertisementId")
        ScrubbedInput advertisementId,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed to advertisement approval for {} by {}", advertisementId, queueUser.getQueueUserId());

        AdvertisementEntity advertisement = advertisementService.findAdvertisementById(advertisementId.getText());
        model.addAttribute("advertisementForm", AdvertisementForm.populate(advertisement));
        return nextPage;
    }

    @PostMapping(value = "/approval/preview", produces = "text/html;charset=UTF-8")
    public String approvalProcess(
        @ModelAttribute("advertisementForm")
        AdvertisementForm advertisementForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed to advertisement approval for {} by {}", advertisementForm.getAdvertisementId(), queueUser.getQueueUserId());
        AdvertisementEntity advertisement = advertisementService.findAdvertisementById(advertisementForm.getAdvertisementId());
        if (null == advertisement) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }

        switch (advertisementForm.getValidateStatus()) {
            case A:
                advertisement.setValidateStatus(ValidateStatusEnum.A);
                break;
            case R:
                advertisement.setValidateStatus(ValidateStatusEnum.R);
                break;
            default:
                LOG.error("Reached unsupported condition={}", advertisement.getValidateStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + advertisement.getValidateStatus());
        }
        advertisement.setValidateByQid(queueUser.getQueueUserId());
        advertisementService.save(advertisement);
        return "redirect:" + "/emp/landing";
    }
}
