package com.noqapp.domain.json.fcm.data.speech;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.google.cloud.texttospeech.v1.AudioEncoding;

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
public class JsonAudioConfig {

    @JsonProperty("audioEncoding")
    private String audioEncoding = AudioEncoding.MP3.name();

    public JsonAudioConfig() {
    }

    public JsonAudioConfig(String audioEncoding) {
        this.audioEncoding = audioEncoding;
    }

    public String getAudioEncoding() {
        return audioEncoding;
    }

    public JsonAudioConfig setAudioEncoding(String audioEncoding) {
        this.audioEncoding = audioEncoding;
        return this;
    }
}
