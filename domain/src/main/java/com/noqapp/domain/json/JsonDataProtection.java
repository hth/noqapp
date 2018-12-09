package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.DataProtectionEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * hitender
 * 2018-12-09 09:07
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
public class JsonDataProtection extends AbstractDomain {

    @JsonProperty("dps")
    private Map<String, DataProtectionEnum> dataProtections = new HashMap<>();

    public Map<String, DataProtectionEnum> getDataProtections() {
        return dataProtections;
    }

    public JsonDataProtection setDataProtections(Map<String, DataProtectionEnum> dataProtections) {
        this.dataProtections = dataProtections;
        return this;
    }

    public JsonDataProtection addDataProtection(String userLevel, DataProtectionEnum dataProtection) {
        this.dataProtections.put(userLevel, dataProtection);
        return this;
    }
}
