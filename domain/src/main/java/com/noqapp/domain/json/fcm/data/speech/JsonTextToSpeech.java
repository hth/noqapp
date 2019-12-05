package com.noqapp.domain.json.fcm.data.speech;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 12/5/19 9:28 AM
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
public class JsonTextToSpeech  {

    @JsonProperty("input")
    private JsonTextInput jsonTextInput;

    @JsonProperty("voice")
    private JsonVoiceInput jsonVoiceInput;

    @JsonProperty("audioConfig")
    private JsonAudioConfig jsonAudioConfig;

    public JsonTextInput getJsonTextInput() {
        return jsonTextInput;
    }

    public JsonTextToSpeech setJsonTextInput(JsonTextInput jsonTextInput) {
        this.jsonTextInput = jsonTextInput;
        return this;
    }

    public JsonVoiceInput getJsonVoiceInput() {
        return jsonVoiceInput;
    }

    public JsonTextToSpeech setJsonVoiceInput(JsonVoiceInput jsonVoiceInput) {
        this.jsonVoiceInput = jsonVoiceInput;
        return this;
    }

    public JsonAudioConfig getJsonAudioConfig() {
        return jsonAudioConfig;
    }

    public JsonTextToSpeech setJsonAudioConfig(JsonAudioConfig jsonAudioConfig) {
        this.jsonAudioConfig = jsonAudioConfig;
        return this;
    }
}
