package com.noqapp.view.controller.business;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.type.FileExtensionTypeEnum;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.helper.CommonHelper;
import com.noqapp.domain.json.xml.XmlBusinessCodeQR;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.CodeQRGeneratorService;
import com.noqapp.service.PdfGenerateService;
import com.noqapp.service.StoreHourService;
import com.noqapp.view.form.business.StoreLandingForm;
import com.noqapp.view.helper.WebUtil;

import com.google.zxing.WriterException;

import org.apache.commons.io.IOUtils;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
@RequestMapping (value = "/business/detail")
public class BusinessDetailController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessDetailController.class);

    private String storeDetail;

    private BizService bizService;
    private CodeQRGeneratorService codeQRGeneratorService;
    private PdfGenerateService pdfGenerateService;
    private BusinessUserService businessUserService;
    private StoreHourService storeHourService;

    @Autowired
    public BusinessDetailController(
        @Value ("${storeDetail:/business/storeDetail}")
        String storeDetail,

        BizService bizService,
        CodeQRGeneratorService codeQRGeneratorService,
        PdfGenerateService pdfGenerateService,
        BusinessUserService businessUserService,
        StoreHourService storeHourService
    ) {
        this.storeDetail = storeDetail;

        this.bizService = bizService;
        this.codeQRGeneratorService = codeQRGeneratorService;
        this.pdfGenerateService = pdfGenerateService;
        this.businessUserService = businessUserService;
        this.storeHourService = storeHourService;
    }

    /**
     * Loading landing page for store with Code QR.
     */
    @GetMapping(value = "/store/{storeId}")
    public String storeLanding(
        @PathVariable("storeId")
        ScrubbedInput storeId,

        @ModelAttribute ("storeLandingForm")
        StoreLandingForm storeLandingForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        List<StoreHourEntity> storeHours = storeHourService.findAllStoreHours(bizStore.getId());

        storeLandingForm
            .setBusinessName(bizStore.getBizName().getBusinessName())
            .setAddress(bizStore.getAddressWrappedFunky())
            .setPhone(bizStore.getPhoneFormatted())
            .setDisplayName(bizStore.getDisplayName())
            .setCategoryName(CommonHelper.findCategoryName(bizStore))
            .setStoreHours(storeHours)
            .setBusinessType(bizStore.getBusinessType());

        try {
            storeLandingForm.setQrFileName(codeQRGeneratorService.createQRImage(bizStore.getCodeQRInALink()));
        } catch (WriterException | IOException e) {
            LOG.error("Failed generating image for codeQR reason={}", e.getLocalizedMessage());
        }

        return storeDetail;
    }

    /**
     * Loading landing page for business with Code QR.
     */
    @GetMapping(value = "/business/{codeQR}")
    public void businessLanding(
        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        @ModelAttribute ("storeLandingForm")
        StoreLandingForm storeLandingForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return;
        }
        LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        try {
            BizNameEntity bizName = bizService.findBizNameByCodeQR(codeQR.getText());
            String fileName = codeQRGeneratorService.createQRImage(bizName.getCodeQRInALink());
            File codeQRFile = FileUtil.getFileFromTmpDir(fileName + "." + FileExtensionTypeEnum.PNG.name().toLowerCase());
            XmlBusinessCodeQR xmlBusinessCodeQR = new XmlBusinessCodeQR()
                .setBusinessName(bizName.getBusinessName())
                .setImageLocationCodeQR(codeQRFile.toURI());

            File file = pdfGenerateService.createPDF(xmlBusinessCodeQR.asXML(), bizName.getBusinessName(), PdfGenerateService.PDF_FOR.BIZ);
            WebUtil.setContentType(file.getName(), response);
            response.setHeader("Content-Disposition", "inline; filename=\"" + "NoQueue_" + bizName.getBusinessName() + ".pdf\"");
            IOUtils.copy(new FileInputStream(file), response.getOutputStream());
            response.flushBuffer();
        } catch (WriterException | IOException e) {
            LOG.error("Failed generating image for codeQR reason={}", e.getLocalizedMessage());
        }
    }
}
