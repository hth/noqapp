package com.noqapp.domain.neo4j;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * hitender
 * 1/27/21 4:10 PM
 */
@NodeEntity(label = "Anomaly")
public class AnomalyN4j {

    @Id
    private String qid;

    @Relationship(type = "CUSTOMER_ID", direction = Relationship.OUTGOING)
    private BusinessCustomerN4j businessCustomerN4j;

    public String getQid() {
        return qid;
    }

    public AnomalyN4j setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public BusinessCustomerN4j getBusinessCustomerN4j() {
        return businessCustomerN4j;
    }

    public AnomalyN4j setBusinessCustomerN4j(BusinessCustomerN4j businessCustomerN4j) {
        this.businessCustomerN4j = businessCustomerN4j;
        return this;
    }
}
