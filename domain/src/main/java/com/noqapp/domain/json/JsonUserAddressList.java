package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 5/16/18 10:12 AM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonUserAddressList extends AbstractDomain {

    @JsonProperty("ads")
    private List<JsonUserAddress> jsonUserAddresses = new ArrayList<>();

    public List<JsonUserAddress> getJsonUserAddresses() {
        return jsonUserAddresses;
    }

    public JsonUserAddressList setJsonUserAddresses(List<JsonUserAddress> jsonUserAddresses) {
        this.jsonUserAddresses = jsonUserAddresses;
        return this;
    }

    @Transient
    public JsonUserAddressList addJsonUserAddresses(JsonUserAddress jsonUserAddress) {
        this.jsonUserAddresses.add(jsonUserAddress);
        return this;
    }

    @Transient
    public void removeJsonUserAddresses(String id) {
        jsonUserAddresses.removeIf(i -> i.getId().equals(id));
    }

    @Transient
    public void markJsonUserAddressesPrimary(String id) {
        jsonUserAddresses.forEach(f -> {
            if (f.getId().equalsIgnoreCase(id)) {
                f.setPrimaryAddress(true);
            }
        });
    }
}
