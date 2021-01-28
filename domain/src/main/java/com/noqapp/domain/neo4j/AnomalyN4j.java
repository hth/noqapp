package com.noqapp.domain.neo4j;

import com.noqapp.domain.types.BusinessTypeEnum;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 1/27/21 4:10 PM
 */
@NodeEntity(label = "Anomaly")
public class AnomalyN4j {

    @Id
    private String qid;

    @Property("businessType")
    private BusinessTypeEnum businessType;

    @Property("businessCustomerIds")
    private List<String> businessCustomerIds = new ArrayList<>();

    public String getQid() {
        return qid;
    }

    public AnomalyN4j setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public AnomalyN4j setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public List<String> getBusinessCustomerIds() {
        return businessCustomerIds;
    }

    public AnomalyN4j setBusinessCustomerIds(List<String> businessCustomerIds) {
        this.businessCustomerIds = businessCustomerIds;
        return this;
    }
}
