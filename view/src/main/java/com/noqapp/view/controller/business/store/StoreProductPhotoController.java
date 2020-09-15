package com.noqapp.view.controller.business.store;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.FileService;
import com.noqapp.service.StoreProductService;
import com.noqapp.view.controller.access.UserProfileController;
import com.noqapp.view.form.FileUploadForm;
import com.noqapp.view.validator.ImageValidator;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 9/13/20 9:32 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/store/product/photo")
public class StoreProductPhotoController {
    private static final Logger LOG = LoggerFactory.getLogger(StoreProductPhotoController.class);

    private String nextPage;
    private String bucketName;

    private StoreProductService storeProductService;
    private BusinessUserStoreService businessUserStoreService;
    private FileService fileService;
    private ImageValidator imageValidator;
    private ApiHealthService apiHealthService;

    @Autowired
    public StoreProductPhotoController(
        @Value("${nextPage:/business/storeProductImage}")
        String nextPage,

        @Value("${aws.s3.bucketName}")
        String bucketName,

        StoreProductService storeProductService,
        BusinessUserStoreService businessUserStoreService,
        FileService fileService,
        ImageValidator imageValidator,
        ApiHealthService apiHealthService
    ) {
        this.nextPage = nextPage;
        this.bucketName = bucketName;

        this.storeProductService = storeProductService;
        this.businessUserStoreService = businessUserStoreService;
        this.fileService = fileService;
        this.imageValidator = imageValidator;
        this.apiHealthService = apiHealthService;
    }

    /** Edit product image. */
    @GetMapping(value = "/{storeId}/{storeProductId}/image")
    public String image(
        @PathVariable("storeId")
        ScrubbedInput storeId,

        @PathVariable("storeProductId")
        ScrubbedInput storeProductId,

        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeId.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Landed on editing product image page storeProductId={} bizStoreId={} qid={} userLevel={}",
            storeProductId,
            storeId,
            queueUser.getQueueUserId(),
            queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        StoreProductEntity storeProduct = storeProductService.findOne(storeProductId.getText());
        model.addAttribute("bizStoreId", storeId);
        model.addAttribute("storeProductId", storeProduct.getId());
        model.addAttribute("image", storeProduct.getProductImage());
        model.addAttribute("bucketName", bucketName);
        return nextPage;
    }

    /** For uploading service image. */
    @PostMapping (value = "/uploadPhoto")
    public String uploadServicePhoto(
        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletRequest httpServletRequest,
        HttpServletResponse response
    ) throws IOException {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("uploading image qid={}", queueUser.getQueueUserId());
        String bizStoreId = httpServletRequest.getParameter("bizStoreId");
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), bizStoreId)) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Upload product image bizStoreId={} qid={} userLevel={}", bizStoreId, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        String storeProductId = httpServletRequest.getParameter("storeProductId");
        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if (isMultipart) {
            MultipartHttpServletRequest multipartHttpRequest = WebUtils.getNativeRequest(httpServletRequest, MultipartHttpServletRequest.class);
            final List<MultipartFile> files = UserProfileController.getMultipartFiles(multipartHttpRequest);

            if (!files.isEmpty()) {
                MultipartFile multipartFile = files.iterator().next();

                imageValidator.validate(multipartFile, result);
                if (result.hasErrors()) {
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    LOG.warn("Failed validation");
                    //Re-direct to prevent resubmit
                    return "redirect:/business/store/product/photo/" + bizStoreId + "/" + storeProductId + "/image.htm";
                }

                try {
                    processImage(queueUser.getQueueUserId(), storeProductId, multipartFile);
                    redirectAttrs.addFlashAttribute("fileUploadForm", fileUploadForm.setMessage("Product image uploaded successfully"));
                    return "redirect:/business/store/product/photo/" + bizStoreId + "/" + storeProductId + "/image.htm";
                } catch (Exception e) {
                    LOG.error("Failed store image upload reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/uploadPhoto",
                        "uploadPhoto",
                        StoreProductPhotoController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }

                return "redirect:/business/store/product/photo/" + bizStoreId + "/" + storeProductId + "/image.htm";
            }
        }
        return "redirect:/business/store/product/photo/" + bizStoreId + "/" + storeProductId + "/image.htm";
    }

    @PostMapping(value = "/uploadPhoto", params = "cancel_Upload")
    public String cancelUploadServicePhoto(HttpServletRequest httpServletRequest) {
        LOG.info("Cancel file upload for store product");
        return "redirect:/business/store/product/" + httpServletRequest.getParameter("bizStoreId") + ".htm";
    }

    @PostMapping(value = "/deletePhoto")
    public String deletePhoto(
        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        HttpServletRequest httpServletRequest,
        HttpServletResponse response
    ) throws IOException {
        LOG.info("Delete store product image");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String bizStoreId = httpServletRequest.getParameter("bizStoreId");
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), bizStoreId)) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Delete product image bizStoreId={} qid={} userLevel={}", bizStoreId, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        String storeProductId = httpServletRequest.getParameter("storeProductId");
        StoreProductEntity storeProduct = storeProductService.findOne(storeProductId);
        fileService.deleteProductImage(queueUser.getQueueUserId(), storeProduct.getProductImage(), storeProduct.getBizStoreId());

        storeProduct.setProductImage(null);
        storeProductService.save(storeProduct);
        return "redirect:/business/store/product/photo/" + bizStoreId + "/" + storeProductId + "/image.htm";
    }

    private void processImage(String qid, String storeProductId, MultipartFile multipartFile) throws IOException {
        BufferedImage bufferedImage = fileService.bufferedImage(multipartFile.getInputStream());
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            fileService.addProductImage(
                qid,
                storeProductId,
                storeProductId + FileUtil.getImageFileExtension(multipartFile.getOriginalFilename(), mimeType),
                bufferedImage);
        }
    }
}
