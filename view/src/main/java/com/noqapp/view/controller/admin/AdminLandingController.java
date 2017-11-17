package com.noqapp.view.controller.admin;

import com.noqapp.domain.site.QueueUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
@RequestMapping(value = "/admin")
public class AdminLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminLandingController.class);

    private String nextPage;

    @Autowired
    public AdminLandingController(
            @Value("${nextPage:/admin/landing}")
            String nextPage
    ) {
        this.nextPage = nextPage;
    }

    @RequestMapping (
            value = "/landing",
            method = RequestMethod.GET,
            produces = "text/html;charset=UTF-8"
    )
    public String adminLanding() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Admin landed qid={}", queueUser.getQueueUserId());
        return nextPage;
    }
}
