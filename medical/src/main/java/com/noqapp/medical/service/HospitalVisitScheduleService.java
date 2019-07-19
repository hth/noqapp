package com.noqapp.medical.service;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;

import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.medical.JsonHospitalVisitSchedule;
import com.noqapp.medical.domain.HospitalVisitScheduleEntity;
import com.noqapp.medical.repository.HospitalVisitScheduleManager;

import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 2019-07-19 13:47
 */
@Service
public class HospitalVisitScheduleService {
    private static final Logger LOG = LoggerFactory.getLogger(HospitalVisitScheduleService.class);

    private HospitalVisitScheduleManager hospitalVisitScheduleManager;

    @Autowired
    public HospitalVisitScheduleService(HospitalVisitScheduleManager hospitalVisitScheduleManager) {
        this.hospitalVisitScheduleManager = hospitalVisitScheduleManager;
    }

    public void updateVisit(String qid, String visitName, String performedByQid, Date immunizationDate, String header) {
        HospitalVisitScheduleEntity hospitalVisitSchedule = new HospitalVisitScheduleEntity()
            .setQueueUserId(qid)
            .setVisitName(visitName)
            .setHeader(header)
            .setVisitedDate(new Date())
            .setPerformedByQid(performedByQid);

        hospitalVisitScheduleManager.save(hospitalVisitSchedule);
    }

    public List<HospitalVisitScheduleEntity> findAll(String qid) {
        return hospitalVisitScheduleManager.findAll(qid);
    }

    @Mobile
    public List<JsonHospitalVisitSchedule> findAllAsJson(String qid) {
        List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules = new ArrayList<>();
        List<HospitalVisitScheduleEntity> medicalImmunizations = findAll(qid);
        for (HospitalVisitScheduleEntity medicalImmunization : medicalImmunizations) {
            jsonHospitalVisitSchedules.add(
                new JsonHospitalVisitSchedule()
                    .setName(medicalImmunization.getVisitName())
                    .setHeader(medicalImmunization.getHeader())
                    .setVisitedDate(DateFormatUtils.format(medicalImmunization.getVisitedDate(), ISO8601_FMT, TimeZone.getTimeZone("UTC")))
                    .setExpectedDate(DateFormatUtils.format(medicalImmunization.getExpectedDate(), ISO8601_FMT, TimeZone.getTimeZone("UTC"))));
        }

        return jsonHospitalVisitSchedules;
    }

    @Mobile List<JsonHospitalVisitSchedule> populateStaticImmunizationData(UserProfileEntity userProfile) {
        return null;
    }
}
