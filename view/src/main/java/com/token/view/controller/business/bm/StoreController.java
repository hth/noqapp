package com.token.view.controller.business.bm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.token.domain.BizStoreEntity;
import com.token.domain.site.TokenUser;
import com.token.service.BizService;
import com.token.utils.ScrubbedInput;
import com.token.view.form.business.StoreLandingForm;

/**
 * User: hitender
 * Date: 12/15/16 8:58 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/business/bm/store")
public class StoreController {
    private static final Logger LOG = LoggerFactory.getLogger(StoreController.class);

    private String nextPage;

    private BizService bizService;

    @Autowired
    public StoreController(
            @Value ("${nextPage:/business/bm/store}")
            String nextPage,

            BizService bizService
    ) {
        this.nextPage = nextPage;
        this.bizService = bizService;
    }

    /**
     * Loading landing page for business.
     *
     * @param storeLandingForm
     * @return
     */
    @RequestMapping (value = "/{storeId}", method = RequestMethod.GET)
    public String landing(
            @PathVariable("storeId")
            ScrubbedInput storeId,

            @ModelAttribute ("storeLandingForm")
            StoreLandingForm storeLandingForm
    ) {
        TokenUser receiptUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());

        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        storeLandingForm
                .setAddress(bizStore.getAddress())
                .setPhone(bizStore.getPhone())
                .setDisplayName(bizStore.getDisplayName());

        return nextPage;
    }
}
