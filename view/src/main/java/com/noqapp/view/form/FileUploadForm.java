package com.noqapp.view.form;

import org.springframework.web.multipart.MultipartFile;

/**
 * hitender
 * 7/16/18 12:08 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
/* https://memorynotfound.com/spring-mvc-file-upload-example-validator/ */
public class FileUploadForm {
    private transient MultipartFile file;

    /* Form success or failure message. */
    private String message;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getMessage() {
        return message;
    }

    public FileUploadForm setMessage(String message) {
        this.message = message;
        return this;
    }
}
