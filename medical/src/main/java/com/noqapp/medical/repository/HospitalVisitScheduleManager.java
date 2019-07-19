package com.noqapp.medical.repository;

import com.noqapp.medical.domain.HospitalVisitScheduleEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-19 13:15
 */
public interface HospitalVisitScheduleManager extends RepositoryManager<HospitalVisitScheduleEntity> {

    List<HospitalVisitScheduleEntity> findAll(String qid);
}
