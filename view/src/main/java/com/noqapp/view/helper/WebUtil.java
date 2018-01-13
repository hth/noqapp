package com.noqapp.view.helper;

import com.noqapp.common.type.FileExtensionTypeEnum;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 1/13/18 1:53 PM
 */
public class WebUtil {

    public static void setContentType(String filename, HttpServletResponse response) {
        String extension = FilenameUtils.getExtension(filename);
        if (extension.endsWith(FileExtensionTypeEnum.JPG.name().toLowerCase()) || extension.endsWith(FileExtensionTypeEnum.JPEG.name().toLowerCase())) {
            response.setContentType("image/jpeg");
        } else if (extension.endsWith("gif")) {
            response.setContentType("image/gif");
        } else if (extension.endsWith(FileExtensionTypeEnum.PDF.name().toLowerCase())) {
            response.setContentType("application/pdf");
        } else {
            response.setContentType("image/png");
        }
    }
}
