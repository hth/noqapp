package com.noqapp.common.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * User: hitender
 * Date: 12/15/19 5:28 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class TextToSpeechConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TextToSpeechConfiguration.class);

    private TextToSpeechClient textToSpeechClient;

    @Autowired
    private TextToSpeechConfiguration(Environment environment) throws IOException {
        LOG.info("TextToSpeech initialization started");
        /* JSON downloaded from IAM & Admin --> firebase-adminsdk ---> then click ---> Create Key. */
        String adminSdk = Objects.requireNonNull(environment.getProperty("build.env")).equalsIgnoreCase("prod")
            ? "conf/noq-app-inc-firebase-adminsdk.json"
            : "conf/noqueue-sandbox-firebase-adminsdk.json";
        InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream(adminSdk);

        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(Objects.requireNonNull(credentialsStream)));
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();

        textToSpeechClient = TextToSpeechClient.create(settings);
    }

    public TextToSpeechClient getTextToSpeechClient() {
        return textToSpeechClient;
    }
}
