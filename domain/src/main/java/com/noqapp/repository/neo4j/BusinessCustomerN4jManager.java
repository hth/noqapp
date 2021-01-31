package com.noqapp.repository.neo4j;

import com.noqapp.domain.neo4j.BusinessCustomerN4j;
import com.noqapp.domain.types.BusinessTypeEnum;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;

/**
 * hitender
 * 1/21/21 12:55 AM
 */
@Repository
public interface BusinessCustomerN4jManager extends Neo4jRepository<BusinessCustomerN4j, Long> {

    @Query("MATCH (c:BusinessCustomer)-[r:CUSTOMER_ID]->(b:BizName) where c.qid = $0 RETURN c, r, b")
    Collection<BusinessCustomerN4j> findCustomerRegisteredToAllBusiness(String qid);

    @Query("MATCH (c:BusinessCustomer)-[r:CUSTOMER_ID]->(b:BizName) where c.qid = $0 and b.businessType = $1 RETURN c, r, b")
    Collection<BusinessCustomerN4j> findCustomerRegisteredToSpecificBusinessType(String qid, BusinessTypeEnum businessType);

    @Query("MATCH (c:BusinessCustomer) WHERE c.lastAccessed < $0 DETACH DELETE c RETURN count(*)")
    long deleteNotAccessedSince(Date since);

    @Query("MATCH (c:BusinessCustomer) WHERE c.qid = $0 DETACH DELETE c RETURN count(*)")
    long deleteByQid(String qid);
}
