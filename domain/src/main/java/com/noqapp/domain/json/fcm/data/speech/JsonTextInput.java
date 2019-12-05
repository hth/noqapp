package com.noqapp.domain.json.fcm.data.speech;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 12/5/19 11:06 AM
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
public class JsonTextInput {

    @JsonProperty("text")
    private String text;

    @JsonProperty("ssml")
    private String ssml;

    public JsonTextInput(String text) {
        this.text = text;
        this.ssml = text;
    }

    public JsonTextInput(String text, String ssml) {
        this.text = text;
        this.ssml = ssml;
    }

    public String getText() {
        return text;
    }

    public JsonTextInput setText(String text) {
        this.text = text;
        return this;
    }

    public String getSsml() {
        return ssml;
    }

    public JsonTextInput setSsml(String ssml) {
        this.ssml = ssml;
        return this;
    }
}
