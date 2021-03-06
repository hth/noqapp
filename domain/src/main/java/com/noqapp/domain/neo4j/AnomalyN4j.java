package com.noqapp.domain.neo4j;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * hitender
 * 1/27/21 4:10 PM
 */
@NodeEntity("Anomaly")
public class AnomalyN4j {

    @Id
    private String qid;

    public String getQid() {
        return qid;
    }

    public AnomalyN4j setQid(String qid) {
        this.qid = qid;
        return this;
    }
}
