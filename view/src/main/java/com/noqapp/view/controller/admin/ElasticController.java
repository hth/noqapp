package com.noqapp.view.controller.admin;

import com.noqapp.domain.site.QueueUser;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.search.elastic.service.ElasticAdministrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * hitender
 * 11/17/17 12:37 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/admin/elastic")
public class ElasticController {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticController.class);

    private String nextPage;

    private ElasticAdministrationService elasticAdministrationService;
    private BizStoreElasticService bizStoreElasticService;

    @Autowired
    public ElasticController(
        @Value("${nextPage:/admin/elastic}")
        String nextPage,

        ElasticAdministrationService elasticAdministrationService,
        BizStoreElasticService bizStoreElasticService
    ) {
        this.nextPage = nextPage;

        this.elasticAdministrationService = elasticAdministrationService;
        this.bizStoreElasticService = bizStoreElasticService;
    }

    @GetMapping
    public String elasticLanding() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Admin landed qid={}", queueUser.getQueueUserId());
        return nextPage;
    }

    @PostMapping(value = "/recreate", produces = "text/html;charset=UTF-8")
    public String recreate() {
        LOG.info("Generate Store Queue HTML");
        elasticAdministrationService.deleteAllIndices();
        bizStoreElasticService.addAllBizStoreToElastic();
        return "redirect:/admin/landing.htm";
    }
}
