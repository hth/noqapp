package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 2/28/17 7:55 PM
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
public class JsonResponse extends AbstractDomain {

    @JsonProperty("r")
    private int response;

    @JsonProperty("d")
    private String data;

    public JsonResponse() {
        //Required default constructor
    }

    public JsonResponse(boolean response) {
        this.response = response ? 1 : 0;
    }

    public JsonResponse(boolean response, String data) {
        this.response = response ? 1 : 0;
        this.data = data;
    }

    public int getResponse() {
        return response;
    }

    public String getData() {
        return data;
    }
}
