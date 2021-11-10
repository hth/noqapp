package com.noqapp.domain.neo4j;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Date;

/**
 * hitender
 * 1/20/21 11:46 PM
 */
@NodeEntity("BusinessCustomer")
public class BusinessCustomerN4j {

    /* A unique constraint exists on codeQR. */
    @Id
    private String businessCustomerId;

    @Property("name")
    private String name;

    @Property("qid")
    private String qid;

    @Relationship(type = "CUSTOMER_ID", direction = Relationship.OUTGOING)
    private BizNameN4j bizNameN4j;

    @Property("lastAccessed")
    private Date lastAccessed;

    public String getBusinessCustomerId() {
        return businessCustomerId;
    }

    public BusinessCustomerN4j setBusinessCustomerId(String businessCustomerId) {
        this.businessCustomerId = businessCustomerId;
        return this;
    }

    public String getName() {
        return name;
    }

    public BusinessCustomerN4j setName(String name) {
        this.name = name;
        return this;
    }

    public String getQid() {
        return qid;
    }

    public BusinessCustomerN4j setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public BizNameN4j getBizNameN4j() {
        return bizNameN4j;
    }

    public BusinessCustomerN4j setBizNameN4j(BizNameN4j bizNameN4j) {
        this.bizNameN4j = bizNameN4j;
        return this;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public BusinessCustomerN4j setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
        return this;
    }

    @Override
    public String toString() {
        return businessCustomerId;
    }
}
