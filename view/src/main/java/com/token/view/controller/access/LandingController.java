package com.token.view.controller.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;

/**
 * User: hitender
 * Date: 12/6/16 8:24 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access")
public class LandingController {
    private static final Logger LOG = LoggerFactory.getLogger(LandingController.class);

    /**
     * Refers to landing.jsp
     */
    @Value ("${nextPage:/access/landing}")
    private String nextPage;

    @Timed
    @ExceptionMetered
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/landing",
            method = RequestMethod.GET
    )
    public String loadForm() {
        LOG.info("Landed on next page");
        return nextPage;
    }

}
