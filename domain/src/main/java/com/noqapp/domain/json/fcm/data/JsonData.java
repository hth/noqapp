package com.noqapp.domain.json.fcm.data;

import com.fasterxml.jackson.annotation.*;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;

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
}
