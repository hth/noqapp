package com.noqapp.domain.neo4j;

import com.noqapp.domain.types.BusinessTypeEnum;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * hitender
 * 1/27/21 4:10 PM
 */
@NodeEntity(label = "Anomaly")
public class AnomalyN4j {

    @Id
    private String qid;

    private BusinessTypeEnum businessType;

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
}
