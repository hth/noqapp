package com.noqapp.domain.common;

import static com.noqapp.common.utils.TextToSpeechForCountry.supportedVoice;

import com.noqapp.domain.json.fcm.data.speech.JsonAudioConfig;
import com.noqapp.domain.json.fcm.data.speech.JsonTextInput;
import com.noqapp.domain.json.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.domain.json.fcm.data.speech.JsonVoiceInput;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;

/**
 * User: hitender
 * Date: 12/8/19 6:21 AM
 */
public class PopulateTextToSpeech {

    public static JsonTextToSpeech nowServingText(String nowServing, String nationalLanguageCode, SsmlVoiceGender female) {
        return new JsonTextToSpeech()
            .setJsonAudioConfig(new JsonAudioConfig())
            .setJsonVoiceInput(new JsonVoiceInput(nationalLanguageCode, supportedVoice(nationalLanguageCode), female.name()))
            .setJsonTextInput(new JsonTextInput(nowServing));
    }
}
