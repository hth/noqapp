package com.noqapp.repository.neo4j;

import com.noqapp.domain.neo4j.NotificationN4j;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 2/17/21 3:02 PM
 */
@Repository
public interface NotificationN4jManager extends Neo4jRepository<NotificationN4j, Long> {

    NotificationN4j findById(@Param("id") String id);

    @Query("MATCH (n:Notification) WHERE n.id = $0 SET n.accessCount = n.accessCount + 1 RETURN n")
    NotificationN4j findByIdAndIncrementCount(String id);
}
