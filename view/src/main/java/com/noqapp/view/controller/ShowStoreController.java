package com.noqapp.view.controller;

import com.noqapp.common.type.FileExtensionTypeEnum;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.service.ShowProfessionalProfileHTMLService;
import com.noqapp.service.BizService;
import com.noqapp.service.ShowHTMLService;
import com.noqapp.view.helper.WebUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: hitender
 * Date: 1/16/17 8:12 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
public class ShowStoreController {
    private static final Logger LOG = LoggerFactory.getLogger(ShowStoreController.class);

    private ShowHTMLService showHTMLService;
    private ShowProfessionalProfileHTMLService showProfessionalProfileHTMLService;
    private BizService bizService;

    @Autowired
    public ShowStoreController(ShowHTMLService showHTMLService, ShowProfessionalProfileHTMLService showProfessionalProfileHTMLService, BizService bizService) {
        this.showHTMLService = showHTMLService;
        this.showProfessionalProfileHTMLService = showProfessionalProfileHTMLService;
        this.bizService = bizService;
    }

    /**
     * Loads biz store page when code scanned is not from our app but some other code scanning app.
     * {@link com.noqapp.domain.BizStoreEntity#getCodeQRInALink}
     * <p>
     * Do not change the mapping as it will break all QR Code Mapping.
     *
     * @return
     */
    @GetMapping(value = "/{codeQR}/q", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String showStoreByCodeQR(@PathVariable("codeQR") ScrubbedInput codeQR) {
        if (Validate.isValidObjectId(codeQR.getText())) {
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR.getText());
            if (null == bizStore) {
                return showHTMLService.showStoreByWebLocation(null);
            }

            switch (bizStore.getBusinessType()) {
                case DO:
                    return showProfessionalProfileHTMLService.showStoreByWebLocation(bizStore);
                default:
                    return showHTMLService.showStoreByWebLocation(bizStore);
            }
        }
        return showHTMLService.showStoreByWebLocation(null);
    }

    /**
     * Loading landing page for business with Code QR.
     */
    @GetMapping(value = "/{codeQR}/b", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String showBusinessByCodeQR(@PathVariable("codeQR") ScrubbedInput codeQR) {
        return showHTMLService.showBusinessByCodeQR(codeQR.getText());
    }

    /**
     * Fetch the requested file image.
     */
    @GetMapping(value = "/i/{fileName}")
    public void getFileImage(
            @PathVariable("fileName")
            ScrubbedInput fileName,

            HttpServletResponse response
    ) {
        LOG.info("Loading image on business page fileName={}", fileName.getText());
        InputStream inputStream = null;
        try {
            WebUtil.setContentType(fileName.getText(), response);
            inputStream = new FileInputStream(FileUtil.getFileFromTmpDir(fileName.getText() + "." + FileExtensionTypeEnum.PNG.name().toLowerCase()));
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            LOG.error("Failed PNG image retrieval error occurred for fileName={} reason={}",
                    fileName.getText(),
                    e.getLocalizedMessage(),
                    e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOG.error("Failed closing stream reason={}", e.getLocalizedMessage(), e);
                }
            }
        }
    }
}
