package com.noqapp.view.controller.business.store.supervisor;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.PurchaseOrderService;
import com.noqapp.service.QueueService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.form.business.InQueueForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
@RequestMapping(value = "/business/store/sup")
public class QueueOrderController {
    private static final Logger LOG = LoggerFactory.getLogger(QueueOrderController.class);

    private int durationInDays;
    private String queue;
    private String queueHistorical;
    private String order;
    private String orderHistorical;

    private UserProfileManager userProfileManager;
    private TokenQueueService tokenQueueService;
    private QueueService queueService;
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    public QueueOrderController(
        @Value("${durationInDays:10}")
        int durationInDays,

        @Value("${queue:/business/inQueue}")
        String queue,

        @Value("${queue:/business/inQueueHistorical}")
        String queueHistorical,

        @Value("${order:/business/purchaseOrder}")
        String order,

        @Value("${orderHistorical:/business/purchaseOrderHistorical}")
        String orderHistorical,

        UserProfileManager userProfileManager,
        TokenQueueService tokenQueueService,
        QueueService queueService,
        PurchaseOrderService purchaseOrderService
    ) {
        this.durationInDays = durationInDays;
        this.queue = queue;
        this.queueHistorical = queueHistorical;
        this.order = order;
        this.orderHistorical = orderHistorical;

        this.userProfileManager = userProfileManager;
        this.tokenQueueService = tokenQueueService;
        this.queueService = queueService;
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping(value = {"/{state}/{codeQR}"}, produces = "text/html;charset=UTF-8")
    public String landing(
        @PathVariable("state")
        ScrubbedInput state,

        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        @ModelAttribute("inQueueForm")
        InQueueForm inQueueForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR.getText());
        if (null == tokenQueue) {
            LOG.warn("Could not find codeQR={} qid={} having access as business user", codeQR.getText(), queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }

        inQueueForm.setQueueName(tokenQueue.getDisplayName())
            .setBusinessType(tokenQueue.getBusinessType())
            .setCodeQR(tokenQueue.getId());

        String nextPage;
        if ("historical".equals(state.getText())) {
            if (MessageOriginEnum.O == tokenQueue.getBusinessType().getMessageOrigin()) {
                List<PurchaseOrderEntity> purchaseOrders = purchaseOrderService.findAllOrderByCodeQR(codeQR.getText(), durationInDays);
                for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
                    UserProfileEntity userProfile = userProfileManager.findByQueueUserId(purchaseOrder.getQueueUserId());
                    purchaseOrder.setCustomerName(userProfile.getName());
                }
                inQueueForm.setPurchaseOrders(purchaseOrders);
                nextPage = orderHistorical;
            } else {
                inQueueForm.setJsonQueuePersonList(queueService.getByCodeQR(codeQR.getText(), durationInDays));
                nextPage = queueHistorical;
            }
        } else {
            if (MessageOriginEnum.O == tokenQueue.getBusinessType().getMessageOrigin()) {
                inQueueForm.setPurchaseOrders(purchaseOrderService.findAllOrderByCodeQR(codeQR.getText()));
                nextPage = order;
            } else {
                inQueueForm.setJsonQueuePersonList(queueService.findAllClientQueuedOrAborted(codeQR.getText()));
                nextPage = queue;
            }
        }

        return nextPage;
    }
}
