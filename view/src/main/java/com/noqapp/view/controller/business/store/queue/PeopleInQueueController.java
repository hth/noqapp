package com.noqapp.view.controller.business.store.queue;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.service.QueueService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.form.business.InQueueForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * hitender
 * 3/5/18 12:01 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/store/queue/people")
public class PeopleInQueueController {
    private static final Logger LOG = LoggerFactory.getLogger(PeopleInQueueController.class);

    private String nextPage;

    private TokenQueueService tokenQueueService;
    private QueueService queueService;

    @Autowired
    public PeopleInQueueController(
        @Value("${nextPage:/business/inQueue}")
        String nextPage,

        TokenQueueService tokenQueueService,
        QueueService queueService
    ) {
        this.nextPage = nextPage;

        this.tokenQueueService = tokenQueueService;
        this.queueService = queueService;
    }

    @GetMapping(value = "/{codeQR}", produces = "text/html;charset=UTF-8")
    public String landing(
        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        @ModelAttribute("inQueueForm")
        InQueueForm inQueueForm
    ) {
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR.getText());
        inQueueForm.setQueueName(tokenQueue.getDisplayName())
                .setBusinessType(tokenQueue.getBusinessType())
                .setCodeQR(tokenQueue.getId())
                .setJsonQueuePersonList(queueService.findAllClientQueuedOrAborted(codeQR.getText()));
        return nextPage;
    }
}
