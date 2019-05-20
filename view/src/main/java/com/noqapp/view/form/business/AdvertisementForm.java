package com.noqapp.view.form.business;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.AdvertisementEntity;
import com.noqapp.domain.types.AdvertisementDisplayEnum;
import com.noqapp.domain.types.AdvertisementTypeEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.view.form.FileUploadForm;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-17 10:36
 */
public class AdvertisementForm extends FileUploadForm implements Serializable {
    private String bizNameId;
    private String title;
    private String shortDescription;
    private List<String> imageUrls = new LinkedList<>();
    private String termAndCondition;
    private List<String> termsAndConditions = new LinkedList<>();
    private ValidateStatusEnum validateStatus = ValidateStatusEnum.I;
    private String publishDate;
    private String endDate;

    private String queueUserId;

    private double[] coordinate;
    /* Defaults to 5 KM radius. */
    private int radius = 5;

    private AdvertisementTypeEnum advertisementType;
    private AdvertisementDisplayEnum advertisementDisplay;
    private String advertisementId;
    private boolean active = false;
    private String imageUrl;

    private List<AdvertisementEntity> advertisements = new LinkedList<>();

    @Transient
    private List<AdvertisementTypeEnum> advertisementTypes = new ArrayList<>(AdvertisementTypeEnum.FOR_BUSINESS);

    @Transient
    private List<AdvertisementDisplayEnum> advertisementDisplays = new ArrayList<>(AdvertisementDisplayEnum.FOR_BUSINESS);

    public AdvertisementForm(String bizNameId) {
        this.bizNameId = bizNameId;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public String getTitle() {
        return title;
    }

    public AdvertisementForm setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public AdvertisementForm setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public AdvertisementForm setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    public String getTermAndCondition() {
        return termAndCondition;
    }

    public AdvertisementForm setTermAndCondition(String termAndCondition) {
        this.termAndCondition = termAndCondition;
        return this;
    }

    public List<String> getTermsAndConditions() {
        return termsAndConditions;
    }

    public AdvertisementForm setTermsAndConditions(List<String> termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
        return this;
    }

    public ValidateStatusEnum getValidateStatus() {
        return validateStatus;
    }

    public AdvertisementForm setValidateStatus(ValidateStatusEnum validateStatus) {
        this.validateStatus = validateStatus;
        return this;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public AdvertisementForm setPublishDate(String publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }

    public AdvertisementForm setEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public AdvertisementForm setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public AdvertisementForm setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public int getRadius() {
        return radius;
    }

    public AdvertisementForm setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public AdvertisementTypeEnum getAdvertisementType() {
        return advertisementType;
    }

    public AdvertisementForm setAdvertisementType(AdvertisementTypeEnum advertisementType) {
        this.advertisementType = advertisementType;
        return this;
    }

    public AdvertisementDisplayEnum getAdvertisementDisplay() {
        return advertisementDisplay;
    }

    public AdvertisementForm setAdvertisementDisplay(AdvertisementDisplayEnum advertisementDisplay) {
        this.advertisementDisplay = advertisementDisplay;
        return this;
    }

    public String getAdvertisementId() {
        return advertisementId;
    }

    public AdvertisementForm setAdvertisementId(String advertisementId) {
        this.advertisementId = advertisementId;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public AdvertisementForm setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public AdvertisementForm setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public List<AdvertisementEntity> getAdvertisements() {
        return advertisements;
    }

    public AdvertisementForm setAdvertisements(List<AdvertisementEntity> advertisements) {
        this.advertisements = advertisements;
        return this;
    }

    public List<AdvertisementTypeEnum> getAdvertisementTypes() {
        return advertisementTypes;
    }

    public AdvertisementForm setAdvertisementTypes(List<AdvertisementTypeEnum> advertisementTypes) {
        this.advertisementTypes = advertisementTypes;
        return this;
    }

    public List<AdvertisementDisplayEnum> getAdvertisementDisplays() {
        return advertisementDisplays;
    }

    public AdvertisementForm setAdvertisementDisplays(List<AdvertisementDisplayEnum> advertisementDisplays) {
        this.advertisementDisplays = advertisementDisplays;
        return this;
    }

    public static AdvertisementForm populate(AdvertisementEntity advertisement) {
        return new AdvertisementForm(advertisement.getBizNameId())
            .setTitle(advertisement.getTitle())
            .setShortDescription(advertisement.getShortDescription())
            .setTermsAndConditions(advertisement.getTermAndConditions() == null ? new LinkedList<>() : advertisement.getTermAndConditions())
            .setImageUrls(advertisement.getImageUrls() == null ? new LinkedList<>() : advertisement.getImageUrls())
            .setValidateStatus(advertisement.getValidateStatus())
            .setPublishDate(DateUtil.dateToString(advertisement.getPublishDate()))
            .setEndDate(DateUtil.dateToString(advertisement.getEndDate()))
            .setQueueUserId(advertisement.getQueueUserId())
            .setCoordinate(advertisement.getCoordinate())
            .setAdvertisementType(advertisement.getAdvertisementType())
            .setAdvertisementDisplay(advertisement.getAdvertisementDisplay())
            .setAdvertisementId(advertisement.getId())
            .setActive(advertisement.isActive());
    }
}
