package com.noqapp.domain.neo4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.StringJoiner;

/**
 * hitender
 * 1/19/21 4:49 PM
 */
@NodeEntity(label = "Person")
public class PersonN4j {
    private static final Logger LOG = LoggerFactory.getLogger(PersonN4j.class);

    /* A unique constraint exists on QID. */
    @Id @Index(unique = true)
    private String qid;

    @Property("name")
    private String name;

    @Relationship(type = "VISITS_TO", direction = Relationship.OUTGOING)
    private Collection<StoreN4j> storeN4js = new ArrayList<>();

    @Relationship(type = "CUSTOMER_ID", direction = Relationship.OUTGOING)
    private Collection<BusinessCustomerN4j> businessCustomerN4js = new ArrayList<>();

    @Relationship(type = "HAS_ANOMALY", direction = Relationship.OUTGOING)
    private AnomalyN4j anomalyN4j;

    @Relationship(type = "VIEWED", direction = Relationship.OUTGOING)
    private Collection<NotificationN4j> notificationN4js;

    @Property("bizNameId")
    private String bizNameId;

    @Property("storeCodeQR")
    private String storeCodeQR;

    @Property("lastAccessed")
    private Date lastAccessed;

    @Property("lng")
    private double longitude;

    @Property("lat")
    private double latitude;

    @Property("points")
    private int points;

    @Property("pv")
    private boolean profileVerified;

    public String getQid() {
        return qid;
    }

    public PersonN4j setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public String getName() {
        return name;
    }

    public PersonN4j setName(String name) {
        this.name = name;
        return this;
    }

    public Collection<StoreN4j> getStoreN4js() {
        return storeN4js;
    }

    public PersonN4j setStoreN4js(Collection<StoreN4j> storeN4js) {
        this.storeN4js = storeN4js;
        return this;
    }

    public PersonN4j addStoreN4j(StoreN4j storeN4j) {
        this.storeN4js.add(storeN4j);
        return this;
    }

    public Collection<BusinessCustomerN4j> getBusinessCustomerN4js() {
        return businessCustomerN4js;
    }

    public PersonN4j setBusinessCustomerN4js(Collection<BusinessCustomerN4j> businessCustomerN4js) {
        this.businessCustomerN4js = businessCustomerN4js;
        return this;
    }

    public PersonN4j addBusinessCustomerN4j(BusinessCustomerN4j businessCustomerN4j) {
        this.businessCustomerN4js.add(businessCustomerN4j);
        return this;
    }

    public AnomalyN4j getAnomalyN4j() {
        return anomalyN4j;
    }

    public PersonN4j setAnomalyN4j(AnomalyN4j anomalyN4j) {
        this.anomalyN4j = anomalyN4j;
        return this;
    }

    public Collection<NotificationN4j> getNotificationN4js() {
        return notificationN4js;
    }

    public PersonN4j setNotificationN4js(Collection<NotificationN4j> notificationN4js) {
        this.notificationN4js = notificationN4js;
        return this;
    }

    public PersonN4j addNotificationN4j(NotificationN4j notificationN4j) {
        this.notificationN4js.add(notificationN4j);
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public PersonN4j setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getStoreCodeQR() {
        return storeCodeQR;
    }

    public PersonN4j setStoreCodeQR(String storeCodeQR) {
        this.storeCodeQR = storeCodeQR;
        return this;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public PersonN4j setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public PersonN4j setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public PersonN4j setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public int getPoints() {
        return points;
    }

    public PersonN4j setPoints(int points) {
        this.points = points;
        return this;
    }

    public boolean isProfileVerified() {
        return profileVerified;
    }

    public PersonN4j setProfileVerified(boolean profileVerified) {
        this.profileVerified = profileVerified;
        return this;
    }

    public PersonN4j isVerified() {
        this.profileVerified = true;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PersonN4j.class.getSimpleName() + "[", "]")
            .add("qid='" + qid + "'")
            .add("customerName='" + name + "'")
            .toString();
    }
}
