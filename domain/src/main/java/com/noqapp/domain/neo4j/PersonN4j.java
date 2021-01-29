package com.noqapp.domain.neo4j;

import com.noqapp.domain.QueueEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Date;
import java.util.StringJoiner;

/**
 * hitender
 * 1/19/21 4:49 PM
 */
@NodeEntity(label = "Person")
public class PersonN4j {
    private static final Logger LOG = LoggerFactory.getLogger(PersonN4j.class);

    @Id
    private String qid;

    @Property("name")
    private String name;

    @Relationship(type = "VISITS_TO", direction = Relationship.OUTGOING)
    private StoreN4j storeN4j;

    @Relationship(type = "CUSTOMER_ID", direction = Relationship.OUTGOING)
    private BusinessCustomerN4j businessCustomerN4j;

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

    public StoreN4j getStoreN4j() {
        return storeN4j;
    }

    public PersonN4j setStoreN4j(StoreN4j storeN4j) {
        this.storeN4j = storeN4j;
        return this;
    }

    public BusinessCustomerN4j getBusinessCustomerN4j() {
        return businessCustomerN4j;
    }

    public PersonN4j setBusinessCustomerN4j(BusinessCustomerN4j businessCustomerN4j) {
        this.businessCustomerN4j = businessCustomerN4j;
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

    public static PersonN4j populate(QueueEntity queue, StoreN4j storeN4j) {
        return new PersonN4j()
            .setStoreN4j(storeN4j)
            .setQid(queue.getQueueUserId())
            .setName(queue.getCustomerName())
            .setLastAccessed(new Date());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PersonN4j.class.getSimpleName() + "[", "]")
            .add("qid='" + qid + "'")
            .add("customerName='" + name + "'")
            .toString();
    }
}
