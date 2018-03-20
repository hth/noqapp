package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.Base64;

/**
 * hitender
 * 3/7/18 6:05 PM
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
public class JsonQueuedMinorPerson extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueuedPerson.class);

    @JsonProperty("t")
    private int token;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty ("n")
    private String customerName = "";

    @JsonProperty ("p")
    private String guardianPhone = "";

    @JsonProperty ("gid")
    private String guardianQueueUserId;

    @JsonProperty ("qu")
    private QueueUserStateEnum queueUserState;

    @JsonProperty ("age")
    private long age;

    @JsonProperty ("g")
    private GenderEnum gender;

    public int getToken() {
        return token;
    }

    public JsonQueuedMinorPerson setToken(int token) {
        this.token = token;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonQueuedMinorPerson setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public JsonQueuedMinorPerson setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public JsonQueuedMinorPerson setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
        return this;
    }

    public String getGuardianQueueUserId() {
        return guardianQueueUserId;
    }

    public JsonQueuedMinorPerson setGuardianQueueUserId(String guardianQueueUserId) {
        this.guardianQueueUserId = guardianQueueUserId;
        return this;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public JsonQueuedMinorPerson setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
        return this;
    }

    public long getAge() {
        return age;
    }

    public JsonQueuedMinorPerson setAge(long age) {
        this.age = age;
        return this;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public JsonQueuedMinorPerson setGender(GenderEnum gender) {
        this.gender = gender;
        return this;
    }

    @Transient
    public String getRecordReferenceId() {
        return Base64.getEncoder().encodeToString((token + "#" + guardianQueueUserId + "#" + queueUserId).getBytes());
    }
}
