package com.noqapp.view.controller.admin;

import com.noqapp.domain.site.QueueUser;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.BizStoreSpatialElastic;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private ScheduledExecutorService executorService;

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

        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @GetMapping
    public String elasticLanding() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Admin landed qid={}", queueUser.getQueueUserId());
        return nextPage;
    }

    @PostMapping(value = "/recreate", produces = "text/html;charset=UTF-8")
    public String recreate() {
        LOG.info("Re-create elastic index started");
        if (elasticAdministrationService.deleteAllIndices()) {
            executorService.schedule(() -> {
                try {
                    boolean createdBizStoreMappingSuccessfully = elasticAdministrationService.addMapping(
                        BizStoreElastic.INDEX,
                        BizStoreElastic.TYPE);

                    boolean createdSpatialMappingSuccessfully = elasticAdministrationService.addMapping(
                        BizStoreSpatialElastic.INDEX,
                        BizStoreSpatialElastic.TYPE);

                    if (createdBizStoreMappingSuccessfully && createdSpatialMappingSuccessfully) {
                        LOG.info("Created Index and Mapping successfully. Adding data to Index/Type");
                        bizStoreElasticService.addAllBizStoreToElastic();
                    } else {
                        LOG.error("Failed re-create elastic index");
                    }
                } catch (Exception e) {
                    LOG.warn("Failed re-creating index reason={}", e.getLocalizedMessage(), e);
                }
            }, 45, TimeUnit.SECONDS);
        } else {
            LOG.warn("Failed deleting elastic index");
        }
        return "redirect:/admin/landing.htm";
    }
}
