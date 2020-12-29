package com.noqapp.domain;

import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ValidateStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * hitender
 * 12/27/20 1:56 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "PUBLISH_JOB")
public class PublishJobEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(PublishJobEntity.class);

    @Field ("QID")
    private String queueUserId;

    @Field("TI")
    private String title;

    @Field("BT")
    private BusinessTypeEnum businessType;

    @Field("BN")
    private String bizNameId;

    @Field ("VB")
    private String validateByQid;

    @Field ("VS")
    private ValidateStatusEnum validateStatus;

    @Field("DS")
    private String description;

    @Field ("HC")
    private List<String> historicalContent;

    @Field("PD")
    private Date publishDate;

    /* When article is flagged by community. */
    @Field("FC")
    private int flagCount;

    @Field("LC")
    private int likeCount;

    public String getQueueUserId() {
        return queueUserId;
    }

    public PublishJobEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PublishJobEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public PublishJobEntity setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public PublishJobEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getValidateByQid() {
        return validateByQid;
    }

    public PublishJobEntity setValidateByQid(String validateByQid) {
        this.validateByQid = validateByQid;
        return this;
    }

    public ValidateStatusEnum getValidateStatus() {
        return validateStatus;
    }

    public PublishJobEntity setValidateStatus(ValidateStatusEnum validateStatus) {
        this.validateStatus = validateStatus;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PublishJobEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getHistoricalContent() {
        return historicalContent;
    }

    public PublishJobEntity setHistoricalContent(List<String> historicalContent) {
        this.historicalContent = historicalContent;
        return this;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public PublishJobEntity setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public int getFlagCount() {
        return flagCount;
    }

    public PublishJobEntity setFlagCount(int flagCount) {
        this.flagCount = flagCount;
        return this;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public PublishJobEntity setLikeCount(int likeCount) {
        this.likeCount = likeCount;
        return this;
    }
}
