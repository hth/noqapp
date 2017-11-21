package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: hitender
 * Date: 9/7/17 6:24 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
public class JsonQueuedPerson extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueuedPerson.class);

    @JsonProperty ("t")
    private int token;

    @JsonProperty ("n")
    private String customerName = "";

    @JsonProperty ("sid")
    private String serverDeviceId = "";

    public int getToken() {
        return token;
    }

    public JsonQueuedPerson setToken(int token) {
        this.token = token;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public JsonQueuedPerson setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getServerDeviceId() {
        return serverDeviceId;
    }

    public JsonQueuedPerson setServerDeviceId(String serverDeviceId) {
        this.serverDeviceId = serverDeviceId;
        return this;
    }
}
