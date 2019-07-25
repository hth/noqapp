package com.noqapp.medical.service;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.medical.JsonHospitalVisitSchedule;
import com.noqapp.domain.types.BooleanReplacementEnum;
import com.noqapp.domain.types.medical.HospitalVisitForEnum;
import com.noqapp.medical.domain.HospitalVisitScheduleEntity;
import com.noqapp.medical.repository.HospitalVisitScheduleManager;
import com.noqapp.medical.visit.Immunization;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.ArrayList;
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
    private UserProfileManager userProfileManager;

    @Autowired
    public HospitalVisitScheduleService(
        HospitalVisitScheduleManager hospitalVisitScheduleManager,
        UserProfileManager userProfileManager
    ) {
        this.hospitalVisitScheduleManager = hospitalVisitScheduleManager;
        this.userProfileManager = userProfileManager;
    }

    @Mobile
    public HospitalVisitScheduleEntity markAsVisited(String id, String qid, String performedByQid) {
        return hospitalVisitScheduleManager.markAsVisited(id, qid, performedByQid);
    }

    @Mobile
    @Secured({"ROLE_S_MANAGER", "ROLE_MEDICAL_TECHNICIAN"})
    public HospitalVisitScheduleEntity removeVisit(String id, String qid) {
        return hospitalVisitScheduleManager.removeVisit(id, qid);
    }

    public List<HospitalVisitScheduleEntity> findAll(String qid) {
        return hospitalVisitScheduleManager.findAll(qid);
    }

    @Mobile
    public List<JsonHospitalVisitSchedule> findAllAsJson(String qid) {
        List<HospitalVisitScheduleEntity> hospitalVisitSchedules = findAll(qid);
        return populateWithHospitalVisitScheduleAsJson(hospitalVisitSchedules);
    }

    public List<HospitalVisitScheduleEntity> findAll(String qid, HospitalVisitForEnum hospitalVisitFor) {
        return hospitalVisitScheduleManager.findAll(qid, hospitalVisitFor);
    }

    @Mobile
    public List<JsonHospitalVisitSchedule> findAllAsJson(String qid, HospitalVisitForEnum hospitalVisitFor) {
        List<HospitalVisitScheduleEntity> hospitalVisitSchedules = findAll(qid, hospitalVisitFor);
        return populateWithHospitalVisitScheduleAsJson(hospitalVisitSchedules);
    }

    private List<JsonHospitalVisitSchedule> populateWithHospitalVisitScheduleAsJson(List<HospitalVisitScheduleEntity> hospitalVisitSchedules) {
        List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules = new ArrayList<>();
        for (HospitalVisitScheduleEntity hospitalVisitSchedule : hospitalVisitSchedules) {
            jsonHospitalVisitSchedules.add(populateHospitalVisitScheduleAsJson(hospitalVisitSchedule));
        }

        return jsonHospitalVisitSchedules;
    }

    @Mobile
    public JsonHospitalVisitSchedule populateHospitalVisitScheduleAsJson(HospitalVisitScheduleEntity hospitalVisitSchedule) {
        String performedBy = null;
        if (StringUtils.isNotBlank(hospitalVisitSchedule.getPerformedByQid())) {
            performedBy = userProfileManager.findByQueueUserId(hospitalVisitSchedule.getPerformedByQid()).getName();
        }

        return new JsonHospitalVisitSchedule()
            .setHospitalVisitScheduleId(hospitalVisitSchedule.getId())
            .setHospitalVisitFor(hospitalVisitSchedule.getHospitalVisitFor())
            .setVisitingFor(hospitalVisitSchedule.getVisitingFor())
            .setHeader(hospitalVisitSchedule.getHeader())
            .setVisitedDate(hospitalVisitSchedule.getVisitedDate() == null ? null : DateFormatUtils.format(hospitalVisitSchedule.getVisitedDate(), ISO8601_FMT, TimeZone.getTimeZone("UTC")))
            .setExpectedDate(DateFormatUtils.format(hospitalVisitSchedule.getExpectedDate(), ISO8601_FMT, TimeZone.getTimeZone("UTC")))
            .setPerformedBy(performedBy);
    }

    @Mobile
    public void addImmunizationRecord(String qid, String dob) {
        LocalDate birthday = DateUtil.asLocalDate(DateUtil.convertToDate(dob, ZoneOffset.UTC));
        if (Period.between(birthday, LocalDate.now()).getYears() < 18) {
            List<HospitalVisitScheduleEntity> hospitalVisitSchedules = Immunization.populateImmunizationVisit(qid, birthday);
            for (HospitalVisitScheduleEntity hospitalVisitSchedule : hospitalVisitSchedules) {
                hospitalVisitScheduleManager.save(hospitalVisitSchedule);
            }
        }
    }

    @Mobile
    public HospitalVisitScheduleEntity modifyVisitingFor(String id, String qid, String visitingFor, BooleanReplacementEnum booleanReplacement, String performedByQid) {
        return hospitalVisitScheduleManager.modifyVisitingFor(id, qid, visitingFor, booleanReplacement, performedByQid);
    }
}
