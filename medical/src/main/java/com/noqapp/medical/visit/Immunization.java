package com.noqapp.medical.visit;

import com.noqapp.common.utils.DateUtil;
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
                .addVisitingFor("BCG")
                .addVisitingFor("Oral Polio Vaccine")
                .addVisitingFor("Hepatitis B Vaccine")
                .setHeader("Birth")
                .setExpectedDate(DateUtil.asDate(birthday.plus(0, ChronoUnit.WEEKS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("DPT/D Tap")
                .addVisitingFor("OPV/IPV")
                .addVisitingFor("Hepatitis B Vaccine")
                .addVisitingFor("HIB")
                .addVisitingFor("Pneumococcal Vaccine")
                .addVisitingFor("Rotavirus")
                .setHeader("6-8 Weeks")
                .setExpectedDate(DateUtil.asDate(birthday.plus(6, ChronoUnit.WEEKS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("DPT/D Tap")
                .addVisitingFor("OPV/IPV")
                .addVisitingFor("Hepatitis B Vaccine")
                .addVisitingFor("HIB")
                .addVisitingFor("Pneumococcal Vaccine")
                .addVisitingFor("Rotavirus")
                .setHeader("10-12 Weeks")
                .setExpectedDate(DateUtil.asDate(birthday.plus(10, ChronoUnit.WEEKS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("DPT/D Tap")
                .addVisitingFor("OPV/IPV")
                .addVisitingFor("Hepatitis B Vaccine")
                .addVisitingFor("HIB")
                .addVisitingFor("Pneumococcal Vaccine")
                .addVisitingFor("Rotavirus")
                .setHeader("14-16 Weeks")
                .setExpectedDate(DateUtil.asDate(birthday.plus(14, ChronoUnit.WEEKS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("Oral Vaccine")
                .addVisitingFor("Flu Vaccine")
                .addVisitingFor("Flu Vaccine")
                .setHeader("6-9 Months")
                .setExpectedDate(DateUtil.asDate(birthday.plus(6, ChronoUnit.MONTHS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("MMR")
                .addVisitingFor("Typhoid Conjugate Vaccine")
                .addVisitingFor("Meningococcal Conjugate Vaccine")
                .setHeader("9-12 Months")
                .setExpectedDate(DateUtil.asDate(birthday.plus(9, ChronoUnit.MONTHS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("Hepatitis A Vaccine 1")
                .addVisitingFor("Hepatitis A Vaccine 2")
                .addVisitingFor("Vericella Vaccine")
                .addVisitingFor("Meningococcal Conjugate Vaccine")
                .setHeader("12 Months Onwards")
                .setExpectedDate(DateUtil.asDate(birthday.plus(12, ChronoUnit.MONTHS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("MMR")
                .addVisitingFor("DPT/D Tap")
                .addVisitingFor("OPV/IPV")
                .addVisitingFor("HIB")
                .addVisitingFor("Pneumococcal Vaccine")
                .addVisitingFor("Flu Vaccine")
                .setHeader("15-18 Months")
                .setExpectedDate(DateUtil.asDate(birthday.plus(15, ChronoUnit.MONTHS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("Typhoid Vaccine/TCV Booster")
                .setHeader("2 Years")
                .setExpectedDate(DateUtil.asDate(birthday.plus(2, ChronoUnit.YEARS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("MMR")
                .addVisitingFor("DPT/OPV")
                .addVisitingFor("Typhoid Vaccine")
                .addVisitingFor("Varicella Vaccine")
                .setHeader("5 Years")
                .setExpectedDate(DateUtil.asDate(birthday.plus(5, ChronoUnit.YEARS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("Typhoid Vaccine")
                .setHeader("8 Years")
                .setExpectedDate(DateUtil.asDate(birthday.plus(8, ChronoUnit.YEARS))));

        visits.add(
            new HospitalVisitScheduleEntity()
                .setQueueUserId(qid)
                .setHospitalVisitFor(HospitalVisitForEnum.IMU)
                .addVisitingFor("TD/D Tap")
                .addVisitingFor("HPV Vaccine")
                .setHeader("10 Years")
                .setExpectedDate(DateUtil.asDate(birthday.plus(10, ChronoUnit.YEARS))));

        return visits;
    }
}
