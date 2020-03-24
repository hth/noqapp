package com.noqapp.portal.medical;

import com.noqapp.service.QueueService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * hitender
 * 3/20/20 1:45 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/portal/medical/dashboard")
public class MedicalDashboard {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalDashboard.class);

    private QueueService queueService;

    public MedicalDashboard(QueueService queueService) {
        this.queueService = queueService;
    }
}
