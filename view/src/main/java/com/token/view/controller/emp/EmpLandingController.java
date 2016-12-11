package com.token.view.controller.emp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.token.domain.site.TokenUser;
import com.token.service.BusinessUserService;
import com.token.view.form.emp.EmpLandingForm;

/**
 * User: hitender
 * Date: 12/11/16 8:18 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/emp")
public class EmpLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(EmpLandingController.class);

    private String empLanding;
    private BusinessUserService businessUserService;

    @Autowired
    public EmpLandingController(
            @Value ("${empLanding:/emp/landing}")
            String empLanding,

            BusinessUserService businessUserService) {
        this.empLanding = empLanding;
        this.businessUserService = businessUserService;
    }

    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public String empLanding(
            @ModelAttribute ("empLandingForm")
            EmpLandingForm empLandingForm
    ) {
        TokenUser tokenUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Employee landed rid={}", tokenUser.getRid());

        empLandingForm.setAwaitingApprovalCount(businessUserService.awaitingApprovalCount());
        return empLanding;
    }
}
