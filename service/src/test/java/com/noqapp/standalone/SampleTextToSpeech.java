package com.noqapp.standalone;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.ListVoicesRequest;
import com.google.cloud.texttospeech.v1.ListVoicesResponse;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.Voice;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

/**
 * Set variable GOOGLE_APPLICATION_CREDENTIALS=/path/to/noqueue-sandbox-firebase-adminsdk.json
 * User: hitender
 * Date: 12/5/19 2:18 PM
 */
public class SampleTextToSpeech {

    public static void main(String[] args) throws Exception {
        new SampleTextToSpeech().synthesizeSsml("No Queue Token number 5 Mr Durga, please visit Dr Vaswani OPD 5");
        new SampleTextToSpeech().synthesizeSsml("No Queue Token संख्या 5 Mr Durga, कृप्या Dr Vaswani ओपीडी 5 पर जाएं");
        List<Voice> voices = new SampleTextToSpeech().listAllSupportedVoices();
        for (Voice voice : voices) {
            ProtocolStringList protocolStringList = voice.getLanguageCodesList();
            for (ByteString byteString : protocolStringList.asByteStringList()) {
                Locale locale = Locale.forLanguageTag(byteString.toString("UTF-8"));
                System.out.println(byteString.toString("UTF-8") + ", " + locale.getLanguage() + ", " + locale.getCountry());
            }
        }
    }

    /**
     * Demonstrates using the Text to Speech client to synthesize text or ssml.
     *
     * <p>Note: ssml must be well-formed according to: (https://www.w3.org/TR/speech-synthesis/
     * Example: <speak>Hello there.</speak>
     *
     * @param ssml the ssml document to be synthesized. (e.g., "<?xml...")
     * @throws Exception on TextToSpeechClient Errors.
     */
    public static ByteString synthesizeSsml(String ssml) throws Exception {
        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the ssml input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setSsml(ssml).build();

            // Build the voice request
            VoiceSelectionParams voice =
                VoiceSelectionParams.newBuilder()
                    .setLanguageCode("hi-IN") // languageCode = "en_us"
                    .setSsmlGender(SsmlVoiceGender.FEMALE) // ssmlVoiceGender = SsmlVoiceGender.FEMALE
                    .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig =
                AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3) // MP3 audio.
                    .setPitch(4)
                    .setSpeakingRate(0.79)
                    .build();

            // Perform the text-to-speech request
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream("output.mp3")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"output.mp3\"");
                return audioContents;
            }
        }
    }

    /**
     * Demonstrates using the Text to Speech client to list the client's supported voices.
     * @throws Exception on TextToSpeechClient Errors.
     */
    public static List<Voice> listAllSupportedVoices() throws Exception {
        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Builds the text to speech list voices request
            ListVoicesRequest request = ListVoicesRequest.getDefaultInstance();

            // Performs the list voices request
            ListVoicesResponse response = textToSpeechClient.listVoices(request);
            List<Voice> voices = response.getVoicesList();

            for (Voice voice : voices) {
                // Display the voice's name. Example: tpc-vocoded
                System.out.format("Name: %s\n", voice.getName());

                // Display the supported language codes for this voice. Example: "en-us"
                List<ByteString> languageCodes = voice.getLanguageCodesList().asByteStringList();
                for (ByteString languageCode : languageCodes) {
                    System.out.format("Supported Language: %s\n", languageCode.toStringUtf8());
                }

                // Display the SSML Voice Gender
                System.out.format("SSML Voice Gender: %s\n", voice.getSsmlGender());

                // Display the natural sample rate hertz for this voice. Example: 24000
                System.out.format("Natural Sample Rate Hertz: %s\n\n",
                    voice.getNaturalSampleRateHertz());
            }
            return voices;
        }
    }
}
