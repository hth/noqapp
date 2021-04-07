package com.noqapp.common.config;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.v3beta1.TranslationServiceClient;
import com.google.cloud.translate.v3beta1.TranslationServiceSettings;

import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * hitender
 * 1/3/21 1:38 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class TranslationConfiguration {
    private static final Logger LOG = getLogger(TranslationConfiguration.class);

    private TranslationServiceClient translationServiceClient;

    @Autowired
    private TranslationConfiguration(Environment environment) throws IOException {
        LOG.info("TextToSpeech initialization started");
        /* JSON downloaded from IAM & Admin --> firebase-adminsdk ---> then click ---> Create Key. */
        String adminSdk = Objects.requireNonNull(environment.getProperty("build.env")).equalsIgnoreCase("prod")
            ? "conf/noq-app-inc-firebase-adminsdk.json"
            : "conf/noqueue-sandbox-firebase-adminsdk.json";
        InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream(adminSdk);

        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(Objects.requireNonNull(credentialsStream)));
        TranslationServiceSettings translationServiceSettings = TranslationServiceSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();

        translationServiceClient = TranslationServiceClient.create(translationServiceSettings);
    }

    public TranslationServiceClient getTranslationServiceClient() {
        return translationServiceClient;
    }
}
