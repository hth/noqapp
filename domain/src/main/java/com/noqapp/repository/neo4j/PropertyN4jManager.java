package com.noqapp.repository.neo4j;

import com.noqapp.domain.neo4j.PropertyN4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 1/19/21 12:12 PM
 */
@Repository
public interface PropertyN4jManager extends Neo4jRepository<PropertyN4j, Long> {
}
