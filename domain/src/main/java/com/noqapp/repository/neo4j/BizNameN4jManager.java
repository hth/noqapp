package com.noqapp.repository.neo4j;

import com.noqapp.domain.neo4j.BizNameN4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 1/21/21 12:11 AM
 */
@Repository
public interface BizNameN4jManager extends Neo4jRepository<BizNameN4j, Long> {

}
