package com.noqapp.view.controller.business.survey;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.SurveyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 10/20/19 6:35 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/survey")
public class SurveyController {
    private static final Logger LOG = LoggerFactory.getLogger(SurveyController.class);

    private String nextPage;
    private String addSurveyFlow;

    private SurveyService surveyService;
    private BusinessUserService businessUserService;

    @Autowired
    public SurveyController(
        @Value("${nextPage:/business/survey/landing}")
        String nextPage,

        @Value("${addSurveyFlow:redirect:/store/addSurvey.htm}")
        String addSurveyFlow,

        SurveyService surveyService,
        BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;
        this.addSurveyFlow = addSurveyFlow;

        this.surveyService = surveyService;
        this.businessUserService = businessUserService;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Landed on survey page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        surveyService.findAll(businessUser.getBizName().getId());

        return nextPage;
    }

    @GetMapping(value = "/add", produces = "text/html;charset=UTF-8")
    public String addQuestionnaire(HttpServletResponse response) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Add survey by business {} qid={} level={}", addSurveyFlow, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        return addSurveyFlow;
    }
}
