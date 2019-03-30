package com.noqapp.view.controller.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 2019-03-30 10:58
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/payout")
public class PayoutLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(PayoutLandingController.class);

    private String nextPage;

    @Autowired
    public PayoutLandingController(
        @Value("${nextPage:/business/payout/landing}")
        String nextPage
    ) {
        this.nextPage = nextPage;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(HttpServletResponse response) throws IOException {
        return nextPage;
    }
}
