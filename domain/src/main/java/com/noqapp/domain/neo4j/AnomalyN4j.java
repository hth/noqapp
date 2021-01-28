package com.noqapp.domain.neo4j;

import com.noqapp.domain.types.BusinessTypeEnum;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * hitender
 * 1/27/21 4:10 PM
 */
@NodeEntity(label = "Anomaly")
public class AnomalyN4j {

    @Id
    private String qid;

    @Relationship(type = "PRESENT", direction = Relationship.OUTGOING)
    private PersonN4j personN4j;

    @Property("lastAccessed")
    private Date lastAccessed;

    private Set<BusinessTypeEnum> businessTypes = new LinkedHashSet<>();

    public String getQid() {
        return qid;
    }

    public AnomalyN4j setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public PersonN4j getPersonN4j() {
        return personN4j;
    }

    public AnomalyN4j setPersonN4j(PersonN4j personN4j) {
        this.personN4j = personN4j;
        return this;
    }

    public Set<BusinessTypeEnum> getBusinessTypes() {
        return businessTypes;
    }

    public AnomalyN4j addBusinessType(BusinessTypeEnum businessType) {
        this.businessTypes.add(businessType);
        return this;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public AnomalyN4j setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
        return this;
    }
}
