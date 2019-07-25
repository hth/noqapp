package com.noqapp.medical.visit;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.types.BooleanReplacementEnum;
import com.noqapp.domain.types.medical.HospitalVisitForEnum;
import com.noqapp.medical.domain.HospitalVisitScheduleEntity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-21 13:48
 */
public class Immunization {

    public static List<HospitalVisitScheduleEntity> populateImmunizationVisit(String qid, LocalDate birthday) {
        List<HospitalVisitScheduleEntity> visits = new LinkedList<>();
        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("BCG", BooleanReplacementEnum.N)
                .addVisitingFor("Oral Polio Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("Hepatitis B Vaccine", BooleanReplacementEnum.N)
                .setHeader("Birth")
                .setExpectedDate(DateUtil.asDate(birthday.plus(0, ChronoUnit.WEEKS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("DPT/D Tap", BooleanReplacementEnum.N)
                .addVisitingFor("OPV/IPV", BooleanReplacementEnum.N)
                .addVisitingFor("Hepatitis B Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("HIB", BooleanReplacementEnum.N)
                .addVisitingFor("Pneumococcal Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("Rotavirus", BooleanReplacementEnum.N)
                .setHeader("6-8 Weeks")
                .setExpectedDate(DateUtil.asDate(birthday.plus(6, ChronoUnit.WEEKS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("DPT/D Tap", BooleanReplacementEnum.N)
                .addVisitingFor("OPV/IPV", BooleanReplacementEnum.N)
                .addVisitingFor("Hepatitis B Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("HIB", BooleanReplacementEnum.N)
                .addVisitingFor("Pneumococcal Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("Rotavirus", BooleanReplacementEnum.N)
                .setHeader("10-12 Weeks")
                .setExpectedDate(DateUtil.asDate(birthday.plus(10, ChronoUnit.WEEKS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("DPT/D Tap", BooleanReplacementEnum.N)
                .addVisitingFor("OPV/IPV", BooleanReplacementEnum.N)
                .addVisitingFor("Hepatitis B Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("HIB", BooleanReplacementEnum.N)
                .addVisitingFor("Pneumococcal Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("Rotavirus", BooleanReplacementEnum.N)
                .setHeader("14-16 Weeks")
                .setExpectedDate(DateUtil.asDate(birthday.plus(14, ChronoUnit.WEEKS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("Oral Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("Flu Vaccine 1", BooleanReplacementEnum.N)
                .addVisitingFor("Flu Vaccine 2", BooleanReplacementEnum.N)
                .setHeader("6-9 Months")
                .setExpectedDate(DateUtil.asDate(birthday.plus(6, ChronoUnit.MONTHS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("MMR", BooleanReplacementEnum.N)
                .addVisitingFor("Typhoid Conjugate Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("Meningococcal Conjugate Vaccine", BooleanReplacementEnum.N)
                .setHeader("9-12 Months")
                .setExpectedDate(DateUtil.asDate(birthday.plus(9, ChronoUnit.MONTHS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("Hepatitis A Vaccine 1", BooleanReplacementEnum.N)
                .addVisitingFor("Hepatitis A Vaccine 2", BooleanReplacementEnum.N)
                .addVisitingFor("Vericella Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("Meningococcal Conjugate Vaccine", BooleanReplacementEnum.N)
                .setHeader("12 Months")
                .setExpectedDate(DateUtil.asDate(birthday.plus(12, ChronoUnit.MONTHS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("MMR", BooleanReplacementEnum.N)
                .addVisitingFor("DPT/D Tap", BooleanReplacementEnum.N)
                .addVisitingFor("OPV/IPV", BooleanReplacementEnum.N)
                .addVisitingFor("HIB", BooleanReplacementEnum.N)
                .addVisitingFor("Pneumococcal Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("Flu Vaccine", BooleanReplacementEnum.N)
                .setHeader("15-18 Months")
                .setExpectedDate(DateUtil.asDate(birthday.plus(15, ChronoUnit.MONTHS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("Typhoid Vaccine/TCV Booster", BooleanReplacementEnum.N)
                .setHeader("2 Years")
                .setExpectedDate(DateUtil.asDate(birthday.plus(2, ChronoUnit.YEARS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("MMR", BooleanReplacementEnum.N)
                .addVisitingFor("DPT/OPV", BooleanReplacementEnum.N)
                .addVisitingFor("Typhoid Vaccine", BooleanReplacementEnum.N)
                .addVisitingFor("Varicella Vaccine", BooleanReplacementEnum.N)
                .setHeader("5 Years")
                .setExpectedDate(DateUtil.asDate(birthday.plus(5, ChronoUnit.YEARS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("Typhoid Vaccine", BooleanReplacementEnum.N)
                .setHeader("8 Years")
                .setExpectedDate(DateUtil.asDate(birthday.plus(8, ChronoUnit.YEARS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("TD/D Tap", BooleanReplacementEnum.N)
                .addVisitingFor("HPV Vaccine", BooleanReplacementEnum.N)
                .setHeader("10 Years")
                .setExpectedDate(DateUtil.asDate(birthday.plus(10, ChronoUnit.YEARS))));

        return visits;
    }
}
