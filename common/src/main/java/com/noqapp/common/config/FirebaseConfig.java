package com.noqapp.common.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

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
 * Date: 6/22/17 7:45 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class FirebaseConfig {
    private static final Logger LOG = LoggerFactory.getLogger(FirebaseConfig.class);

    private static FirebaseOptions options;
    private static FirebaseApp firebaseApp;
    private static FirebaseAuth firebaseAuth;
    private static FirebaseMessaging firebaseMessaging;

    private static String prodDB = "https://noq-app-inc.firebaseio.com";
    private static String sandboxDB = "https://noqueue-sandbox.firebaseio.com";

    @Autowired
    private FirebaseConfig(Environment environment) {
        if (null == options) {
            LOG.info("FirebaseApp initialization started");
            /* JSON downloaded from IAM & Admin --> firebase-adminsdk ---> then click ---> Create Key. */
            String adminSdk = Objects.requireNonNull(environment.getProperty("build.env")).equalsIgnoreCase("prod")
                ? "conf/noq-app-inc-firebase-adminsdk.json"
                : "conf/noqueue-sandbox-firebase-adminsdk.json";
            InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream(adminSdk);
            try {
                GoogleCredentials googleCredentials = GoogleCredentials.fromStream(Objects.requireNonNull(credentialsStream));
                options = FirebaseOptions.builder()
                        .setCredentials(googleCredentials)
                        .setDatabaseUrl(Objects.requireNonNull(environment.getProperty("build.env")).equalsIgnoreCase("prod") ? prodDB : sandboxDB)
                        .build();
            } catch (IOException e) {
                LOG.error("Failed to initialize reason={}", e.getLocalizedMessage(), e);
            }
        } else {
            LOG.warn("FirebaseApp options already initialized");
        }

        /* Continue initialization. */
        doInitializeFirebaseAuth();
        LOG.info("FirebaseApp with databaseUrl={} initialized", options.getDatabaseUrl());
    }

    private void doInitializeFirebaseApp() {
        try {
            if (null == firebaseApp) {
                firebaseApp = FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            LOG.error("Failed initialize FirebaseApp reason={}", e.getLocalizedMessage(), e);
        }
    }

    private void doInitializeFirebaseAuth() {
        if (null == firebaseApp) {
            doInitializeFirebaseApp();
        }

        try {
            if (null == firebaseAuth) {
                assertNotNull(firebaseApp, "FirebaseApp should not be null");
                firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
            }
        } catch (Exception e) {
            LOG.error("Failed initialize firebaseAuth reason={}", e.getLocalizedMessage(), e);
        }

        try {
            if(null == firebaseMessaging) {
                firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp);
            }
        } catch (Exception e) {
            LOG.error("Failed initialize firebaseMessaging reason={}", e.getLocalizedMessage(), e);
        }
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseMessaging getFirebaseMessaging() {
        return firebaseMessaging;
    }
}
