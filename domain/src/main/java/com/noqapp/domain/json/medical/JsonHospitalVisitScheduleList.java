package com.noqapp.domain.json.medical;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-19 08:36
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable",
    "unused"
})
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonHospitalVisitScheduleList extends AbstractDomain {

    @JsonProperty("hvs")
    private List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules = new ArrayList<>();

    public List<JsonHospitalVisitSchedule> getJsonHospitalVisitSchedules() {
        return jsonHospitalVisitSchedules;
    }

    public JsonHospitalVisitScheduleList setJsonHospitalVisitSchedules(List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules) {
        this.jsonHospitalVisitSchedules = jsonHospitalVisitSchedules;
        return this;
    }

    public JsonHospitalVisitScheduleList addJsonHospitalVisitSchedule(JsonHospitalVisitSchedule jsonHospitalVisitSchedule) {
        this.jsonHospitalVisitSchedules.add(jsonHospitalVisitSchedule);
        return this;
    }
}
