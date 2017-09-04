package com.noqapp.view.controller.business;

import com.google.zxing.WriterException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

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

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BizService;
import com.noqapp.service.CodeQRGeneratorService;
import com.noqapp.type.FileExtensionTypeEnum;
import com.noqapp.utils.FileUtil;
import com.noqapp.utils.ScrubbedInput;
import com.noqapp.view.form.business.StoreLandingForm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
@RequestMapping (value = "/business/store/detail")
public class StoreDetailController {
    private static final Logger LOG = LoggerFactory.getLogger(StoreDetailController.class);

    private String nextPage;

    private BizService bizService;
    private CodeQRGeneratorService codeQRGeneratorService;

    @Autowired
    public StoreDetailController(
            @Value ("${nextPage:/business/storeDetail}")
            String nextPage,

            BizService bizService,
            CodeQRGeneratorService codeQRGeneratorService
    ) {
        this.nextPage = nextPage;
        this.bizService = bizService;
        this.codeQRGeneratorService = codeQRGeneratorService;
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
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        List<StoreHourEntity> storeHours = bizService.findAllStoreHours(bizStore.getId());
        storeLandingForm
                .setAddress(bizStore.getAddress())
                .setPhone(bizStore.getPhone())
                .setDisplayName(bizStore.getDisplayName())
                .setStoreHours(storeHours);

        try {
            storeLandingForm.setQrFileName(codeQRGeneratorService.createQRImage(bizStore.getCodeQRInALink()));
        } catch (WriterException | IOException e) {
            LOG.error("Failed generating image for codeQR reason={}", e.getLocalizedMessage());
        }

        return nextPage;
    }

    /**
     *
     * @param fileName
     * @return
     */
    @RequestMapping (value = "/i/{fileName}", method = RequestMethod.GET)
    public void getQRFilename(
            @PathVariable("fileName")
            ScrubbedInput fileName,

            HttpServletResponse response
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        InputStream inputStream = null;
        try {
            setContentType(fileName.getText(), response);
            inputStream = new FileInputStream(FileUtil.getFileFromTmpDir(fileName.getText() + "." + FileExtensionTypeEnum.PNG.name()));
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            LOG.error("Failed PNG image retrieval error occurred for user={} reason={}",
                    queueUser.getQueueUserId(), e.getLocalizedMessage(), e);
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    private void setContentType(String filename, HttpServletResponse response) {
        String extension = FilenameUtils.getExtension(filename);
        if (extension.endsWith("jpg") || extension.endsWith("jpeg")) {
            response.setContentType("image/jpeg");
        } else if (extension.endsWith("gif")) {
            response.setContentType("image/gif");
        } else {
            response.setContentType("image/png");
        }
    }
}
