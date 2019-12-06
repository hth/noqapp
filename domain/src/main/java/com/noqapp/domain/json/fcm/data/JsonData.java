package com.noqapp.domain.json.fcm.data;

import com.noqapp.domain.json.fcm.data.speech.JsonAudioConfig;
import com.noqapp.domain.json.fcm.data.speech.JsonTextInput;
import com.noqapp.domain.json.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.domain.json.fcm.data.speech.JsonVoiceInput;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 3/7/17 11:07 AM
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
public abstract class JsonData {

    @JsonProperty("f")
    private FirebaseMessageTypeEnum firebaseMessageType;

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    @JsonProperty("imageURL")
    private String imageURL;

    @JsonProperty("textToSpeech")
    private JsonTextToSpeech jsonTextToSpeech;

    JsonData(FirebaseMessageTypeEnum firebaseMessageType) {
        this.firebaseMessageType = firebaseMessageType;
    }

    public String getTitle() {
        return title;
    }

    public JsonData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBody() {
        return body;
    }

    public JsonData setBody(String body) {
        this.body = body;
        return this;
    }

    public String getImageURL() {
        return imageURL;
    }

    public JsonData setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public JsonTextToSpeech getJsonTextToSpeech() {
        return jsonTextToSpeech;
    }

    public JsonData setJsonTextToSpeech(JsonTextToSpeech jsonTextToSpeech) {
        this.jsonTextToSpeech = jsonTextToSpeech;
        return this;
    }

    public JsonData setJsonTextToSpeech(String message) {
        this.jsonTextToSpeech = new JsonTextToSpeech()
            .setJsonTextInput(new JsonTextInput(message))
            .setJsonVoiceInput(new JsonVoiceInput())
            .setJsonAudioConfig(new JsonAudioConfig());
        return this;
    }
}
