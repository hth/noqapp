package com.noqapp.domain.neo4j;

import com.noqapp.domain.QueueEntity;

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

    @Property("lastAccessed")
    private Date lastAccessed;

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

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public PersonN4j setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
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
