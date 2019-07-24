package com.noqapp.medical.repository;

import com.noqapp.domain.types.BooleanReplacementEnum;
import com.noqapp.domain.types.medical.HospitalVisitForEnum;
import com.noqapp.medical.domain.HospitalVisitScheduleEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 2019-07-19 13:15
 */
public interface HospitalVisitScheduleManager extends RepositoryManager<HospitalVisitScheduleEntity> {

    List<HospitalVisitScheduleEntity> findAll(String qid);
    List<HospitalVisitScheduleEntity> findAll(String qid, HospitalVisitForEnum hospitalVisitFor);

    HospitalVisitScheduleEntity removeVisit(String id, String qid);
    HospitalVisitScheduleEntity markAsVisited(String id, String qid, String performedByQid);

    HospitalVisitScheduleEntity modifyVisitingFor(String id, String qid, String visitingFor, BooleanReplacementEnum booleanReplacement, String performedByQid);

    Stream<HospitalVisitScheduleEntity> notifyAllUpComingHospitalVisit();
    void increaseNotificationCount(String id);
}
