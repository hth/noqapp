package com.noqapp.view.controller.business.survey;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.QuestionnaireEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.SurveyService;
import com.noqapp.view.form.business.QuestionnaireForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

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
    private String questionnaireDetailPage;
    private String addSurveyFlow;

    private SurveyService surveyService;
    private BusinessUserService businessUserService;

    @Autowired
    public SurveyController(
        @Value("${nextPage:/business/survey/landing}")
        String nextPage,

        @Value("${nextPage:/business/survey/questionnaireDetail}")
        String questionnaireDetailPage,

        @Value("${addSurveyFlow:redirect:/store/addSurvey.htm}")
        String addSurveyFlow,

        SurveyService surveyService,
        BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;
        this.questionnaireDetailPage = questionnaireDetailPage;
        this.addSurveyFlow = addSurveyFlow;

        this.surveyService = surveyService;
        this.businessUserService = businessUserService;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("questionnaireForm")
        QuestionnaireForm questionnaireForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Landed on survey page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        List<QuestionnaireEntity> questionnaires = surveyService.findAll(businessUser.getBizName().getId());
        questionnaireForm.setQuestionnaires(questionnaires);

        return nextPage;
    }

    @GetMapping(value = "/add", produces = "text/html;charset=UTF-8")
    public String addQuestionnaire(HttpServletResponse response) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Add survey by business {} qid={} level={}", addSurveyFlow, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        return addSurveyFlow;
    }

    @GetMapping(
        value = "/live/rating",
        headers = "Accept=application/json",
        produces = "application/json")
    @ResponseBody
    public String liveRating() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Live rating business {} qid={} level={}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        return surveyService.getRecentOverallRating(businessUser.getBizName().getId()).asJson();
    }

    @GetMapping(value = "/questionnaireDetail/{questionnaireId}", produces = "text/html;charset=UTF-8")
    public String questionnaireDetail(
        @PathVariable("questionnaireId")
        ScrubbedInput questionnaireId,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Live rating business {} qid={} level={}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        QuestionnaireEntity questionnaire = surveyService.findByQuestionnaireId(questionnaireId.getText());
        model.addAttribute("questionnaire", questionnaire);

        return questionnaireDetailPage;
    }

    @GetMapping(value = "/dashboard", produces = "text/html;charset=UTF-8")
    public String dashboard() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Live rating business {} qid={} level={}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        return surveyService.getRecentOverallRating(businessUser.getBizName().getId()).asJson();
    }
}
