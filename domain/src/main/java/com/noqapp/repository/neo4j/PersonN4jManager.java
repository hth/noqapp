package com.noqapp.repository.neo4j;

import com.noqapp.domain.neo4j.PersonN4j;
import com.noqapp.domain.neo4j.StoreN4j;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * hitender
 * 1/19/21 5:14 PM
 */
@Repository
public interface PersonN4jManager extends Neo4jRepository<PersonN4j, Long> {

    /** No need for @Query as it defaults executes the query. */
    //@Query("MATCH (p:Person) WHERE p.qid = $qid RETURN p")
    PersonN4j findByQid(@Param("qid") String qid);

    /** $0 represents the first parameter. */
    @Query("MATCH (p:Person) WHERE p.qid = $0 RETURN p")
    PersonN4j findByQidWithQuery(String qid);

    @Query("MATCH (p:Person)-[:VISITED_TO]->(s:Store) where p.qid = $0 return s")
    List<StoreN4j> findAllStoreVisitedByQid(String qid);

    @Query("MATCH (p:Person)-[:VISITED_TO]->(s:Store) where p.qid = $0 return s.bizNameId")
    Set<String> findAllBusinessVisitedByQid(String qid);
}
