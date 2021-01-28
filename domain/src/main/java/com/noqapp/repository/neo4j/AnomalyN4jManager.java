package com.noqapp.repository.neo4j;

import com.noqapp.domain.neo4j.AnomalyN4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 1/27/21 4:14 PM
 */
@Repository
public interface AnomalyN4jManager extends Neo4jRepository<AnomalyN4j, Long> {

}
