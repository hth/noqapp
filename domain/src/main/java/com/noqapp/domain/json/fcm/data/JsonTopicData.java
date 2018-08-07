package com.noqapp.domain.json.fcm.data;

import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.FCMTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 7/31/18 5:36 PM
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
public class JsonTopicData {
    private static final Logger LOG = LoggerFactory.getLogger(JsonTopicData.class);

    private JsonTopicQueueData jsonTopicQueueData;
    private JsonTopicOrderData jsonTopicOrderData;
    private JsonDisplayData jsonDisplayData;

    public JsonTopicData(FCMTypeEnum fcmType, FirebaseMessageTypeEnum firebaseMessageType) {
        switch (fcmType) {
            case Q:
                jsonTopicQueueData = new JsonTopicQueueData(firebaseMessageType, fcmType);
                break;
            case O:
                jsonTopicOrderData = new JsonTopicOrderData(firebaseMessageType, fcmType);
                break;
            case D:
                jsonDisplayData = new JsonDisplayData(firebaseMessageType, fcmType);
                break;
            default:
                LOG.error("Reached unreachable condition {}", fcmType);
                throw new UnsupportedOperationException("Reached unreachable condition");
        }
    }

    public JsonTopicQueueData getJsonTopicQueueData() {
        return jsonTopicQueueData;
    }

    public JsonTopicData setJsonTopicQueueData(JsonTopicQueueData jsonTopicQueueData) {
        this.jsonTopicQueueData = jsonTopicQueueData;
        return this;
    }

    public JsonTopicOrderData getJsonTopicOrderData() {
        return jsonTopicOrderData;
    }

    public JsonTopicData setJsonTopicOrderData(JsonTopicOrderData jsonTopicOrderData) {
        this.jsonTopicOrderData = jsonTopicOrderData;
        return this;
    }

    public JsonDisplayData getJsonDisplayData() {
        return jsonDisplayData;
    }

    public JsonTopicData setJsonDisplayData(JsonDisplayData jsonDisplayData) {
        this.jsonDisplayData = jsonDisplayData;
        return this;
    }
}
