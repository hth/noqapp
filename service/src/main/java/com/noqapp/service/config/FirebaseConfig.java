package com.noqapp.service.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseCredential;
import com.google.firebase.auth.FirebaseCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;

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
@Configuration
public class FirebaseConfig {
    private static final Logger LOG = LoggerFactory.getLogger(FirebaseConfig.class);

    private static FirebaseOptions options;
    private static FirebaseApp firebaseApp;
    private static FirebaseAuth firebaseAuth;

    public FirebaseConfig() {
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

        /* Continue initialization. */
        getFirebaseApp();
        getFirebaseAuth();
        LOG.info("Initialized firebaseApp with databaseUrl={}", options.getDatabaseUrl());
    }

    private FirebaseApp getFirebaseApp() {
        if (firebaseApp == null) {
            return firebaseApp = FirebaseApp.initializeApp(options);
        }
        return firebaseApp;
    }

    public FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
        }
        return firebaseAuth;
    }
}
