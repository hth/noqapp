package com.noqapp.service.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseCredential;
import com.google.firebase.auth.FirebaseCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

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

    private FirebaseConfig() {
        if (null == options) {
            LOG.info("FirebaseApp initialization started");
            /* JSON downloaded from IAM & Admin --> firebase-adminsdk ---> then click ---> Create Key. */
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("conf/noq-app-inc-firebase-adminsdk.json");
            try {
                FirebaseCredential firebaseCredential = FirebaseCredentials.fromCertificate(serviceAccount);
                options = new FirebaseOptions.Builder()
                        .setCredential(firebaseCredential)
                        .setDatabaseUrl("https://noq-app-inc.firebaseio.com")
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
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
}
