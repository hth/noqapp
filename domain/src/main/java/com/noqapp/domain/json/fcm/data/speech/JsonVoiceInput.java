package com.noqapp.domain.json.fcm.data.speech;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;

/**
 * User: hitender
 * Date: 12/5/19 11:11 AM
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
public class JsonVoiceInput {

    @JsonProperty("languageCode")
    private String languageCode = "en-gb";

    @JsonProperty("name")
    private String name = "en-GB-Standard-A";

    @JsonProperty("ssmlGender")
    private String ssmlGender = SsmlVoiceGender.NEUTRAL.name();

    public JsonVoiceInput() {
    }

    public JsonVoiceInput(String languageCode, String name, String ssmlGender) {
        this.languageCode = languageCode;
        this.name = name;
        this.ssmlGender = ssmlGender;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getName() {
        return name;
    }

    public String getSsmlGender() {
        return ssmlGender;
    }
}
