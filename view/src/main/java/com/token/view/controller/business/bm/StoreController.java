package com.token.view.controller.business.bm;

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

import com.token.domain.BizStoreEntity;
import com.token.domain.site.TokenUser;
import com.token.service.BizService;
import com.token.service.CodeQRGeneratorService;
import com.token.type.FileExtensionTypeEnum;
import com.token.utils.FileUtil;
import com.token.utils.ScrubbedInput;
import com.token.view.form.business.StoreLandingForm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
@RequestMapping (value = "/business/bm/store")
public class StoreController {
    private static final Logger LOG = LoggerFactory.getLogger(StoreController.class);

    private String nextPage;

    private BizService bizService;
    private CodeQRGeneratorService codeQRGeneratorService;

    @Autowired
    public StoreController(
            @Value ("${nextPage:/business/bm/store}")
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
        TokenUser receiptUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());

        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        storeLandingForm
                .setAddress(bizStore.getAddress())
                .setPhone(bizStore.getPhone())
                .setDisplayName(bizStore.getDisplayName());

        try {
            storeLandingForm.setQrFileName(codeQRGeneratorService.createQRImage(bizStore.getCodeQR()));
        } catch (WriterException | IOException e) {
            LOG.error("error generating code={}", e.getLocalizedMessage());
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
        TokenUser receiptUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());
        InputStream inputStream = null;
        try {
            setContentType(fileName.getText(), response);
            inputStream = new FileInputStream(FileUtil.getFileFromTmpDir(fileName.getText() + "." + FileExtensionTypeEnum.PNG.name()));
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            LOG.error("PNG image retrieval error occurred for user={} reason={}",
                    receiptUser.getRid(), e.getLocalizedMessage(), e);
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
