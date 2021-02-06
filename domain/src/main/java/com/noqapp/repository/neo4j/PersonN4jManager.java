package com.noqapp.repository.neo4j;

import com.noqapp.domain.neo4j.PersonN4j;
import com.noqapp.domain.neo4j.StoreN4j;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

    @Query("MATCH (p:Person) WHERE p.qid = $0 SET p.lastAccessed = $1 RETURN p")
    PersonN4j findByQidWithQuery(String qid, Date now);

    @Query("MATCH (p:Person)-[:VISITS_TO]->(s:Store) where p.qid = $0 RETURN s")
    List<StoreN4j> findAllStoreVisitedByQid(String qid);

    @Query("MATCH (p:Person)-[:VISITS_TO]->(s:Store) where p.qid = $0 RETURN s.bizNameId")
    Set<String> findAllBusinessVisitedByQid(String qid);

    @Query("MATCH (p:Person) WHERE p.lastAccessed < $0 DETACH DELETE p RETURN count(*)")
    long deleteNotAccessedSince(Date since);

    @Query("MATCH (n) WHERE NOT (n)--() DELETE n return count(*)")
    long deleteOrphanNodes();

    @Query("MATCH (p:Person) WHERE p.qid = $0 DETACH DELETE p RETURN count(*)")
    long detachAndDelete(String qid);
}