package com.noqapp.domain.neo4j;

import com.noqapp.domain.types.BusinessTypeEnum;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * hitender
 * 1/21/21 12:06 AM
 */
@NodeEntity(label = "BizName")
public class BizNameN4j {

    @Id
    private String id;

    @Property("codeQR")
    private String codeQR;

    @Property("businessType")
    private BusinessTypeEnum businessType;

    @Property("businessName")
    private String businessName;

    public String getId() {
        return id;
    }

    public BizNameN4j setId(String id) {
        this.id = id;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public BizNameN4j setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public BizNameN4j setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public BizNameN4j setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }
}
