package com.noqapp.domain.json.fcm.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 1/7/17 1:42 AM
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
@JsonInclude (JsonInclude.Include.NON_NULL)
@Deprecated/* Since notification message was preventing from reading data in payload when app is in background. */
public class JsonNotification {

    @JsonProperty ("title")
    private String title;

    @JsonProperty ("title-loc-key")
    private String titleLocKey;

    @JsonProperty ("title-loc-args")
    private String[] titleLocArgs;

    @JsonProperty ("body")
    private String body;

    @JsonProperty ("loc-key")
    private String locKey;

    @JsonProperty ("loc-args")
    private String[] locArgs;

    public String getTitle() {
        return title;
    }

    public JsonNotification setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBody() {
        return body;
    }

    public JsonNotification setBody(String body) {
        this.body = body;
        return this;
    }

    public JsonNotification setTitleLocKey(String titleLocKey) {
        this.titleLocKey = titleLocKey;
        return this;
    }

    public JsonNotification setTitleLocArgs(String[] titleLocArgs) {
        this.titleLocArgs = titleLocArgs;
        return this;
    }

    public JsonNotification setLocKey(String locKey) {
        this.locKey = locKey;
        return this;
    }

    public JsonNotification setLocArgs(String[] locArgs) {
        this.locArgs = locArgs;
        return this;
    }
}
