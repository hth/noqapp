package com.noqapp.domain;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;
import static com.noqapp.domain.TokenQueueEntity.TOPICS;

import com.noqapp.domain.types.IncidentEventEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 5/17/21 9:28 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "INCIDENT_EVENT")
public class IncidentEventEntity extends BaseEntity {

    @Field("IE")
    private IncidentEventEnum incidentEvent;

    @Field("QID")
    private String qid;

    /* Format Longitude and then Latitude. */
    @Field("COR")
    private double[] coordinate;

    @NotNull
    @Field("AD")
    private String address;

    @Field("AR")
    private String area;

    @Field("TO")
    private String town;

    @Field("DT")
    private String district;

    @Field("ST")
    private String state;

    @Field("SS")
    private String stateShortName;

    /* Postal code could be empty for few countries. */
    @Field("PC")
    private String postalCode;

    @Field("CC")
    private String country;

    @NotNull
    @Field("CS")
    private String countryShortName;

    @Field("TI")
    private String title;

    @Field("DS")
    private String description;

    @Field("IM")
    private List<String> images;

    @Field("IV")
    private List<String> incidentVideo;

    @Field("EV")
    private int expectedView;

    @Field("VC")
    private int viewCount;

    public IncidentEventEnum getIncidentEvent() {
        return incidentEvent;
    }

    public IncidentEventEntity setIncidentEvent(IncidentEventEnum incidentEvent) {
        this.incidentEvent = incidentEvent;
        return this;
    }

    public String getQid() {
        return qid;
    }

    public IncidentEventEntity setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public IncidentEventEntity setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public IncidentEventEntity setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getArea() {
        return area;
    }

    public IncidentEventEntity setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public IncidentEventEntity setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public IncidentEventEntity setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public IncidentEventEntity setState(String state) {
        this.state = state;
        return this;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public IncidentEventEntity setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public IncidentEventEntity setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public IncidentEventEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public IncidentEventEntity setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public IncidentEventEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public IncidentEventEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getImages() {
        return images;
    }

    public IncidentEventEntity setImages(List<String> images) {
        this.images = images;
        return this;
    }

    public List<String> getIncidentVideo() {
        return incidentVideo;
    }

    public IncidentEventEntity setIncidentVideo(List<String> incidentVideo) {
        this.incidentVideo = incidentVideo;
        return this;
    }

    public int getExpectedView() {
        return expectedView;
    }

    public IncidentEventEntity setExpectedView(int expectedView) {
        this.expectedView = expectedView;
        return this;
    }

    public int getViewCount() {
        return viewCount;
    }

    public IncidentEventEntity setViewCount(int viewCount) {
        this.viewCount = viewCount;
        return this;
    }

    @Transient
    public String getTopicWellFormatted() {
        return TOPICS + incidentEvent.getAppendTopic() + UNDER_SCORE + incidentEvent.name() + UNDER_SCORE + id;
    }
}
