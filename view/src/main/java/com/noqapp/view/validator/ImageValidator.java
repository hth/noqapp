package com.noqapp.view.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 * hitender
 * 7/16/18 11:30 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class ImageValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ImageValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        try {
            MultipartFile file = (MultipartFile) target;

            if (file.isEmpty() || file.getSize() == 0) {
                LOG.error("Please select a file to upload");
                errors.rejectValue("file", "field.fileNotUploaded", new Object[]{""}, "Please select a file to upload");
            }

            if (!errors.hasErrors()) {
                String s = file.getContentType().toLowerCase();
                if (!s.equals("image/jpg") && !s.equals("image/jpeg") && !s.equals("image/png")) {
                    LOG.error("Supported file formats are JPEG or PNG");
                    errors.rejectValue(
                        "file",
                        "field.fileNotSupported",
                        new Object[]{"JPEG or PNG"},
                        "Supported file formats are JPEG or PNG");
                }

                if (file.getSize() > 0) {
                    // Get length of file in bytes
                    long fileSizeInBytes = file.getSize();
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    long fileSizeInMB = fileSizeInKB / 1024;

                    if (fileSizeInMB > 5) {
                        LOG.error("Selected file size exceeds 5MB");
                        errors.rejectValue("file", "field.fileSizeExceed", new Object[]{"5MB"}, "Selected file size exceeds 5MB");
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Failed validating image file reason={}", e.getLocalizedMessage(), e);
            errors.reject("field.fileNotUploaded");
        }
    }
}
