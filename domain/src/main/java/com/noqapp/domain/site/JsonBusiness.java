package com.noqapp.domain.site;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.ExternalPermissionEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 2/5/18 9:43 PM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonBusiness extends AbstractDomain {

    @JsonProperty("id")
    private String bizId;

    @JsonProperty ("n")
    private String bizName;

    @JsonProperty ("e")
    private String externalAccessId;

    @JsonProperty ("aid")
    private String approverQID;

    @JsonProperty ("ep")
    private ExternalPermissionEnum externalPermission;

    public String getBizId() {
        return bizId;
    }

    public JsonBusiness setBizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public String getBizName() {
        return bizName;
    }

    public JsonBusiness setBizName(String bizName) {
        this.bizName = bizName;
        return this;
    }

    public String getExternalAccessId() {
        return externalAccessId;
    }

    public JsonBusiness setExternalAccessId(String externalAccessId) {
        this.externalAccessId = externalAccessId;
        return this;
    }

    public String getApproverQID() {
        return approverQID;
    }

    public JsonBusiness setApproverQID(String approverQID) {
        this.approverQID = approverQID;
        return this;
    }

    public ExternalPermissionEnum getExternalPermission() {
        return externalPermission;
    }

    public JsonBusiness setExternalPermission(ExternalPermissionEnum externalPermission) {
        this.externalPermission = externalPermission;
        return this;
    }
}
