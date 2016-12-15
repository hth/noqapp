package com.token.view.controller.business.bm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO complete me
 * User: hitender
 * Date: 12/15/16 9:00 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/business/bm")
public class BusinessManagerLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessManagerLandingController.class);

    private String nextPage;


}
