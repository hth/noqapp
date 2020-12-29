package com.noqapp.domain;

import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ValidateStatusEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * hitender
 * 2019-01-02 18:13
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "PUBLISH_ARTICLE")
@CompoundIndexes(value = {
    @CompoundIndex(name = "publish_article_idx", def = "{'QID': -1}", unique = false, background = true)
})
public class PublishArticleEntity extends BaseEntity {

    @Field ("QID")
    private String queueUserId;

    @Field("TI")
    private String title;

    @Field("BT")
    private BusinessTypeEnum businessType;

    @Field("BC")
    private String bizCategoryId;

    @Field ("VB")
    private String validateByQid;

    @Field ("VS")
    private ValidateStatusEnum validateStatus;

    @Field("DS")
    private String description;

    @Field("BI")
    private String bannerImage;

    @Field ("HC")
    private List<String> historicalContent;

    @Field("PD")
    private Date publishDate;

    /* When article is flagged by community. */
    @Field("FC")
    private int flagCount;

    @Field("LC")
    private int likeCount;
    //TODO shared count

    public String getQueueUserId() {
        return queueUserId;
    }

    public PublishArticleEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PublishArticleEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public PublishArticleEntity setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public PublishArticleEntity setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getValidateByQid() {
        return validateByQid;
    }

    public PublishArticleEntity setValidateByQid(String validateByQid) {
        this.validateByQid = validateByQid;
        return this;
    }

    public ValidateStatusEnum getValidateStatus() {
        return validateStatus;
    }

    public PublishArticleEntity setValidateStatus(ValidateStatusEnum validateStatus) {
        this.validateStatus = validateStatus;
        return this;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public PublishArticleEntity setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PublishArticleEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getHistoricalContent() {
        return historicalContent;
    }

    public PublishArticleEntity setHistoricalContent(List<String> historicalContent) {
        this.historicalContent = historicalContent;
        return this;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public PublishArticleEntity setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public int getFlagCount() {
        return flagCount;
    }

    public PublishArticleEntity setFlagCount(int flagCount) {
        this.flagCount = flagCount;
        return this;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public PublishArticleEntity setLikeCount(int likeCount) {
        this.likeCount = likeCount;
        return this;
    }
}
