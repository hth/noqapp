package com.noqapp.domain.json.fcm.data;

import com.noqapp.domain.types.FCMTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 8/1/18 6:43 PM
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
public class JsonDisplayData extends JsonData {

    @JsonProperty("ft")
    private FCMTypeEnum fcmType;

    JsonDisplayData(FirebaseMessageTypeEnum firebaseMessageType, FCMTypeEnum fcmType) {
        super(firebaseMessageType);
        this.fcmType = fcmType;
    }

    public FCMTypeEnum getFcmType() {
        return fcmType;
    }
}
