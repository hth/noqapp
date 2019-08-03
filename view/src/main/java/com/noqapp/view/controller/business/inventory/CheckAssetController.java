package com.noqapp.view.controller.business.inventory;

import com.noqapp.domain.site.QueueUser;
import com.noqapp.inventory.service.CheckAssetService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.view.form.StoreManagerForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 2019-07-30 07:58
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/inventory")
public class CheckAssetController {
    private static final Logger LOG = LoggerFactory.getLogger(CheckAssetController.class);

    private String nextPage;

    private CheckAssetService checkAssetService;
    private BusinessUserService businessUserService;

    @Autowired
    public CheckAssetController(
        @Value("${nextPage:/business/inventory/landing}")
        String nextPage,

        CheckAssetService checkAssetService,
        BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;

        this.checkAssetService = checkAssetService;
        this.businessUserService = businessUserService;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("storeManagerForm")
        StoreManagerForm storeManagerForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        return nextPage;
    }

}
