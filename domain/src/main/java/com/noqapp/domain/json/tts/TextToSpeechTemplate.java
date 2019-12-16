package com.noqapp.domain.json.tts;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Objects;

/**
 * User: hitender
 * Date: 12/16/19 2:54 AM
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
public class TextToSpeechTemplate extends AbstractDomain implements Serializable {

    @JsonProperty("l")
    @Field("L")
    private String languageTag;

    @JsonProperty("t")
    @Field("T")
    private String template;

    public String getLanguageTag() {
        return languageTag;
    }

    public TextToSpeechTemplate setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public TextToSpeechTemplate setTemplate(String template) {
        this.template = template;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextToSpeechTemplate that = (TextToSpeechTemplate) o;
        return languageTag.equals(that.languageTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languageTag);
    }
}
