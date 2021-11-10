package com.noqapp.domain.neo4j;

import com.noqapp.domain.types.BusinessTypeEnum;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

/**
 * hitender
 * 1/21/21 12:06 AM
 */
@NodeEntity("BizName")
public class BizNameN4j {

    /* A unique constraint exists on id. */
    @Id
    private String id;

    @Property("codeQR")
    private String codeQR;

    @Property("businessType")
    private BusinessTypeEnum businessType;

    @Property("businessName")
    private String businessName;

    @Relationship(type = "LOCATION", direction = Relationship.OUTGOING)
    private LocationN4j location;

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

    public LocationN4j getLocation() {
        return location;
    }

    public BizNameN4j setLocation(LocationN4j location) {
        this.location = location;
        return this;
    }
}
