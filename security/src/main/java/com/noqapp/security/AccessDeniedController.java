package com.noqapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: hitender
 * Date: 11/18/16 3:14 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access")
public class AccessDeniedController {
    private static final Logger LOG = LoggerFactory.getLogger(AccessDeniedController.class);

    /**
     * Handles and retrieves the denied JSP page. This is shown whenever a regular user
     * tries to access an admin only page.
     *
     * @return the name of the JSP page
     */
    @PreAuthorize ("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN', 'ROLE_TECHNICIAN', 'ROLE_SUPERVISOR', 'ROLE_Q_SUPERVISOR', 'ROLE_S_MANAGER', 'ROLE_M_ADMIN')")
    @RequestMapping (value = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        LOG.debug("Received request to show denied page");
        return "denied";
    }
}
