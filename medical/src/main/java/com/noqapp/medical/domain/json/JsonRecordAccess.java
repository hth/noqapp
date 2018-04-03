package com.noqapp.medical.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * hitender
 * 4/3/18 6:08 PM
 */
@SuppressWarnings ({
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
public class JsonRecordAccess extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonRecordAccess.class);

    @JsonProperty("raq")
    private String recordAccessedQid;

    @JsonProperty("rad")
    private Date recordAccessedDate;

    public String getRecordAccessedQid() {
        return recordAccessedQid;
    }

    public JsonRecordAccess setRecordAccessedQid(String recordAccessedQid) {
        this.recordAccessedQid = recordAccessedQid;
        return this;
    }

    public Date getRecordAccessedDate() {
        return recordAccessedDate;
    }

    public JsonRecordAccess setRecordAccessedDate(Date recordAccessedDate) {
        this.recordAccessedDate = recordAccessedDate;
        return this;
    }
}
