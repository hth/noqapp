package com.noqapp.repository.neo4j;

import com.noqapp.domain.neo4j.AnomalyN4j;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * hitender
 * 1/27/21 4:14 PM
 */
@Repository
public interface AnomalyN4jManager extends Neo4jRepository<AnomalyN4j, Long> {

    @Query("MATCH (a:Anomaly) WHERE a.qid = $0 DELETE a RETURN count(*)")
    long deleteByQid(String qid);

    @Query("MATCH (a:Anomaly) WHERE a.lastAccessed < $0 DELETE a RETURN count(*)")
    long deleteNotAccessedSince(Date since);
}
