package com.noqapp.service;

import com.noqapp.common.config.TranslationConfiguration;

import com.google.cloud.translate.v3beta1.LocationName;
import com.google.cloud.translate.v3beta1.TranslateTextRequest;
import com.google.cloud.translate.v3beta1.TranslateTextResponse;
import com.google.cloud.translate.v3beta1.Translation;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * hitender
 * 1/3/21 12:17 PM
 */
@Service
public class LanguageTranslationService {
    private static final Logger LOG = LoggerFactory.getLogger(LanguageTranslationService.class);

    private TranslationConfiguration translationConfiguration;

    @Value("${firebase.projectId}")
    private String firebaseProjectId;

    @Value("${targetLanguages}")
    private String[] targetLanguages;

    @Autowired
    public LanguageTranslationService(TranslationConfiguration translationConfiguration) {
        this.translationConfiguration = translationConfiguration;
    }

    public Map<String, String> translateText(String text) {
        Map<String, String> translatedBody = new HashMap<>();
        for (String targetLanguage : targetLanguages) {
            translatedBody.put(targetLanguage, doTextTranslation(targetLanguage, text));
        }

        translatedBody.put("en", text);
        return translatedBody;
    }

    public Map<String, String> translateText(String targetLanguage, String text) {
        if (StringUtils.isNotBlank(targetLanguage)) {
            Map<String, String> translatedBody = new HashMap<>();
            if (!"en".equals(targetLanguage)) {
                translatedBody.put(targetLanguage, doTextTranslation(targetLanguage, text));
            }
            translatedBody.put("en", text);
            return translatedBody;
        } else {
            return translateText(text);
        }
    }

    /** Translate text. */
    private String doTextTranslation(String targetLanguage, String text) {
        LocationName parent = LocationName.of(firebaseProjectId, "global");

        // Supported Mime Types: https://cloud.google.com/translate/docs/supported-formats
        TranslateTextRequest request =
            TranslateTextRequest.newBuilder()
                .setParent(parent.toString())
                .setMimeType("text/plain")
                .setTargetLanguageCode(targetLanguage)
                .addContents(text)
                .build();

        TranslateTextResponse response = translationConfiguration.getTranslationServiceClient().translateText(request);

        // Display the translation for each input text provided
        for (Translation translation : response.getTranslationsList()) {
            LOG.info("Translated text {}", translation.getTranslatedText());
        }

        return response.getTranslationsList().get(0).getTranslatedText();
    }
}
