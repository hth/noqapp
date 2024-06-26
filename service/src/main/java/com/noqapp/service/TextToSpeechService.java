package com.noqapp.service;

import static com.google.cloud.texttospeech.v1.SsmlVoiceGender.FEMALE;
import static com.google.cloud.texttospeech.v1.SsmlVoiceGender.MALE;
import static com.noqapp.common.utils.TextToSpeechForCountry.foreignLanguageCode;
import static com.noqapp.common.utils.TextToSpeechForCountry.nationalLanguageCode;

import com.noqapp.common.config.TextToSpeechConfiguration;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.CustomTextToSpeechEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.common.PopulateTextToSpeech;
import com.noqapp.domain.json.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.domain.json.tts.TextToSpeechTemplate;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.TextToSpeechTypeEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;

import com.google.cloud.texttospeech.v1.ListVoicesRequest;
import com.google.cloud.texttospeech.v1.ListVoicesResponse;
import com.google.cloud.texttospeech.v1.Voice;
import com.google.protobuf.ByteString;

import org.apache.commons.text.StringSubstitutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 12/15/19 2:25 AM
 */
@Service
public class TextToSpeechService {
    private static final Logger LOG = LoggerFactory.getLogger(TextToSpeechService.class);

    private final String nowServingHindi = "No Queue token ${currentlyServingToken}, ${queueServiceName}, krupya ${goTo} kee or jaayeah";
    private final String nowServingEnglish = "No Queue token ${currentlyServingToken}, for ${queueServiceName}, please proceed to ${goTo}";

    private QueueManager queueManager;
    private BizStoreManager bizStoreManager;
    private TextToSpeechConfiguration textToSpeechConfiguration;
    private CustomTextToSpeechService customTextToSpeechService;

    @Autowired
    public TextToSpeechService(
        QueueManager queueManager,
        BizStoreManager bizStoreManager,
        TextToSpeechConfiguration textToSpeechConfiguration,
        CustomTextToSpeechService customTextToSpeechService
    ) {
        this.queueManager = queueManager;
        this.bizStoreManager = bizStoreManager;
        this.textToSpeechConfiguration = textToSpeechConfiguration;
        this.customTextToSpeechService = customTextToSpeechService;
    }

    /**
     * https://cloud.google.com/text-to-speech/docs/voices
     * https://stackoverflow.com/questions/36681232/android-tts-male-voices
     * https://www.journaldev.com/21904/android-text-to-speech-tts
     * https://stackoverflow.com/questions/3058919/text-to-speechtts-android
     * https://android-developers.googleblog.com/2009/09/introduction-to-text-to-speech-in.html
     * https://o7planning.org/en/10503/android-text-to-speech-tutorial
     *
     * @param goTo
     * @param codeQR
     * @param tokenQueue
     * @return
     */
    public List<JsonTextToSpeech> populateTextToSpeech(String goTo, String codeQR, TokenQueueEntity tokenQueue) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        QueueEntity queue = queueManager.findOne(codeQR, tokenQueue.getCurrentlyServing());
        Map<String, String> valuesMap = populateNowServingMap(goTo, tokenQueue, queue);
        CustomTextToSpeechEntity customTextToSpeech = customTextToSpeechService.findByBizNameId(bizStore.getBizName().getId());

        List<JsonTextToSpeech> jsonTextToSpeeches;
        if (null == customTextToSpeech) {
            jsonTextToSpeeches = defaultTextToSpeeches(tokenQueue.getBusinessType(), bizStore.getCountryShortName(), valuesMap);
        } else {
            jsonTextToSpeeches = customTextToSpeeches(valuesMap, customTextToSpeech);
        }
        return jsonTextToSpeeches;
    }

    private List<JsonTextToSpeech> customTextToSpeeches(Map<String, String> valuesMap, CustomTextToSpeechEntity customTextToSpeech) {
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        List<JsonTextToSpeech> jsonTextToSpeeches = new LinkedList<>();
        Set<TextToSpeechTemplate> textToSpeechTemplates = customTextToSpeech.getTextToSpeechTemplates().get(TextToSpeechTypeEnum.SN.name());
        for (TextToSpeechTemplate textToSpeechTemplate : textToSpeechTemplates) {
            jsonTextToSpeeches.add(PopulateTextToSpeech.nowServingText(sub.replace(textToSpeechTemplate.getTemplate()), textToSpeechTemplate.getLanguageTag(), FEMALE));
        }

        return jsonTextToSpeeches;
    }

    @NotNull
    private List<JsonTextToSpeech> defaultTextToSpeeches(BusinessTypeEnum businessType, String countryShortName, Map<String, String> valuesMap) {
        StringSubstitutor sub = new StringSubstitutor(valuesMap);

        List<JsonTextToSpeech> jsonTextToSpeeches = new LinkedList<>();
        switch (businessType) {
            case RS:
            case RSQ:
            case FT:
            case FTQ:
            case BA:
            case BAQ:
            case ST:
            case STQ:
            case CD:
            case CDQ:
            case SM:
            case MT:
            case SC:
            case GS:
            case GSQ:
            case CF:
            case CFQ:
            case HS:
            case PH:
            case PW:
            case MU:
            case TA:
            case NC:
            case BK:
            case PA:
            case DO:
                switch (countryShortName) {
                    case "IN":
                        String languageCode = nationalLanguageCode(countryShortName);
                        if (null != languageCode) {
                            jsonTextToSpeeches.add(PopulateTextToSpeech.nowServingText(sub.replace(nowServingHindi), languageCode, MALE));
                        }

                        languageCode = foreignLanguageCode(countryShortName);
                        jsonTextToSpeeches.add(PopulateTextToSpeech.nowServingText(sub.replace(nowServingEnglish), languageCode, FEMALE));
                        break;
                    case "US":
                        languageCode = nationalLanguageCode(countryShortName);
                        if (null != languageCode) {
                            jsonTextToSpeeches.add(PopulateTextToSpeech.nowServingText(sub.replace(nowServingEnglish), languageCode, MALE));
                        }
                        break;
                }
                break;
            default:
                LOG.error("Reached unreachable condition. Add more business type.");
                throw new UnsupportedOperationException("Reached unreachable condition");
        }
        return jsonTextToSpeeches;
    }

    @NotNull
    private Map<String, String> populateNowServingMap(String goTo, TokenQueueEntity tokenQueue, QueueEntity queue) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("currentlyServingToken", queue.getDisplayToken());
        valuesMap.put("queueServiceName", tokenQueue.getDisplayName());
        valuesMap.put("goTo", goTo);
        return valuesMap;
    }

    public Set<String> supportedLanguages() {
        List<Voice> voices = listAllSupportedVoices();

        Set<String> languages = new HashSet<>();
        for (Voice voice : voices) {

            /* Display the supported language codes for this voice. Example: "en-us". */
            List<ByteString> languageCodes = voice.getLanguageCodesList().asByteStringList();
            for (ByteString languageCode : languageCodes) {
                languages.add(languageCode.toStringUtf8());
            }
        }

        return languages;
    }

    public Set<Locale> supportedSpeechLocale() {
        Set<String> supportedLanguages = supportedLanguages();

        Set<Locale> supportedSpeechLocale = new HashSet<>();
        for (String language : supportedLanguages) {
            supportedSpeechLocale.add(Locale.forLanguageTag(language));
        }

        return supportedSpeechLocale;
    }

    /**
     * Text to Speech client to list the client's supported voices.
     */
    private List<Voice> listAllSupportedVoices() {
        /* Builds the text to speech list voices request. */
        ListVoicesRequest request = ListVoicesRequest.getDefaultInstance();

        /* Performs the list voices request. */
        ListVoicesResponse response = textToSpeechConfiguration.getTextToSpeechClient().listVoices(request);
        return response.getVoicesList();
    }
}
