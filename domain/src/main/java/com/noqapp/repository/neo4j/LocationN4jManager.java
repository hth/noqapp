package com.noqapp.repository.neo4j;

import com.noqapp.domain.neo4j.LocationN4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 2/6/21 11:19 AM
 */
@Repository
public interface LocationN4jManager extends Neo4jRepository<LocationN4j, Long> {

}
