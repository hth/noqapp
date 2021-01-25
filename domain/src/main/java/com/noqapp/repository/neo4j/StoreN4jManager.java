package com.noqapp.repository.neo4j;

import com.noqapp.domain.neo4j.StoreN4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 1/19/21 5:15 PM
 */
@Repository
public interface StoreN4jManager extends Neo4jRepository<StoreN4j, Long> {

}
