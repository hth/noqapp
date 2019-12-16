package com.noqapp.domain;

import com.noqapp.domain.json.tts.TextToSpeechTemplate;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;
import java.util.Set;

/**
 * User: hitender
 * Date: 12/13/19 7:18 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "CUSTOM_TTS")
@CompoundIndexes(value = {
    @CompoundIndex(name = "custom_tts_idx", def = "{'BN': -1}", unique = true)
})
@RedisHash("CUSTOM_TTS")
public class CustomTextToSpeechEntity extends BaseEntity {

    @Field("BN")
    private String bizNameId;

    @Field("CM")
    private Map<String, Set<TextToSpeechTemplate>> textToSpeechTemplates;

    public String getBizNameId() {
        return bizNameId;
    }

    public CustomTextToSpeechEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public Map<String, Set<TextToSpeechTemplate>> getTextToSpeechTemplates() {
        return textToSpeechTemplates;
    }

    public CustomTextToSpeechEntity setTextToSpeechTemplates(Map<String, Set<TextToSpeechTemplate>> textToSpeechTemplates) {
        this.textToSpeechTemplates = textToSpeechTemplates;
        return this;
    }
}
