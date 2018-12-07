package com.noqapp.view.controller.admin;

import com.noqapp.domain.site.QueueUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    public ElasticController(
        @Value("${nextPage:/admin/elastic}")
        String nextPage
    ) {
        this.nextPage = nextPage;
    }

    @GetMapping
    public String elasticLanding() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Admin landed qid={}", queueUser.getQueueUserId());
        return nextPage;
    }
}
