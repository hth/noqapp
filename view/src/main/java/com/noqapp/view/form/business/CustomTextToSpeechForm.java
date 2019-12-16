package com.noqapp.view.form.business;

import com.noqapp.domain.CustomTextToSpeechEntity;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.domain.types.TextToSpeechTypeEnum;

import java.util.Map;

/**
 * User: hitender
 * Date: 12/15/19 12:57 AM
 */
public class CustomTextToSpeechForm {

    private TextToSpeechTypeEnum textToSpeechType;
    private String languageTag = "en-US";
    private String template = "NoQueue token number ${currentlyServing}, please visit ${displayName}, in ${goTo}";
    private ActionTypeEnum actionType;

    private CustomTextToSpeechEntity customTextToSpeech;

    /* Supported tts. */
    private Map<String, String> supportedSpeechLocaleMap;
    private Map<String, String> textToSpeechTypes = TextToSpeechTypeEnum.asMapWithNameAsKey();

    public TextToSpeechTypeEnum getTextToSpeechType() {
        return textToSpeechType;
    }

    public CustomTextToSpeechForm setTextToSpeechType(TextToSpeechTypeEnum textToSpeechType) {
        this.textToSpeechType = textToSpeechType;
        return this;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public CustomTextToSpeechForm setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public CustomTextToSpeechForm setTemplate(String template) {
        this.template = template;
        return this;
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public CustomTextToSpeechForm setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
        return this;
    }

    public CustomTextToSpeechEntity getCustomTextToSpeech() {
        return customTextToSpeech;
    }

    public CustomTextToSpeechForm setCustomTextToSpeech(CustomTextToSpeechEntity customTextToSpeech) {
        this.customTextToSpeech = customTextToSpeech;
        return this;
    }

    public Map<String, String> getSupportedSpeechLocaleMap() {
        return supportedSpeechLocaleMap;
    }

    public CustomTextToSpeechForm setSupportedSpeechLocaleMap(Map<String, String> supportedSpeechLocaleMap) {
        this.supportedSpeechLocaleMap = supportedSpeechLocaleMap;
        return this;
    }

    public Map<String, String> getTextToSpeechTypes() {
        return textToSpeechTypes;
    }

    public CustomTextToSpeechForm setTextToSpeechTypes(Map<String, String> textToSpeechTypes) {
        this.textToSpeechTypes = textToSpeechTypes;
        return this;
    }
}
