package com.noqapp.domain.json.fcm.data;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Constants;
import com.noqapp.domain.json.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /* For reference to message id. */
    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    @JsonProperty("translatedBody")
    private Map<String, String> translatedBody = new HashMap<>();

    @JsonProperty("imageURL")
    private String imageURL;

    @JsonProperty("textToSpeeches")
    private List<JsonTextToSpeech> jsonTextToSpeeches;

    JsonData(FirebaseMessageTypeEnum firebaseMessageType) {
        this.firebaseMessageType = firebaseMessageType;

        /* Added id to save the message to db on mobile device. */
        this.id = CommonUtil.generateHexFromObjectId();
    }

    public String getId() {
        return id;
    }

    /* Set the mongoId when message needs to be validated. */
    public JsonData setId(String id) {
        this.id = id;
        return this;
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

    public Map<String, String> getTranslatedBody() {
        return translatedBody;
    }

    public JsonData setTranslatedBody(Map<String, String> translatedBody) {
        this.translatedBody = translatedBody;
        return this;
    }

    public JsonData addTranslatedBody(String targetLanguage, String text) {
        this.translatedBody.put(targetLanguage, text);
        return this;
    }

    public String getImageURL() {
        return imageURL;
    }

    public JsonData setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public List<JsonTextToSpeech> getJsonTextToSpeeches() {
        return jsonTextToSpeeches;
    }

    public JsonData setJsonTextToSpeeches(List<JsonTextToSpeech> jsonTextToSpeeches) {
        this.jsonTextToSpeeches = jsonTextToSpeeches;
        return this;
    }
}
