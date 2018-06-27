package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 5/29/18 5:29 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "S3_FILE")
@CompoundIndexes({
        @CompoundIndex(name = "s3_idx", def = "{'QID': 1}")
})
public class S3FileEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String qid;

    @NotNull
    @Field ("QR")
    private String codeQR;

    @NotNull
    @Field ("FL")
    private String filename;

    @NotNull
    @Field ("LO")
    private String location;

    public S3FileEntity(String qid, String filename, String location) {
        this.qid = qid;
        this.filename = filename;
        this.location = location;
    }

    public String getQid() {
        return qid;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public S3FileEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public String getLocation() {
        return location;
    }
}
