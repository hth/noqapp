package com.noqapp.service.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
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

    private FirebaseApp firebaseApp;
    private FirebaseAuth firebaseAuth;

    public FirebaseConfig() {
        try {
            LOG.info("Initialized firebaseApp started");
            /* Downloaded from IAM & Admin --> firebase-adminsdk ---> then click ---> Create Key. */
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("conf/noq-app-inc-firebase-adminsdk.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                    .setDatabaseUrl("https://noq-app-inc.firebaseio.com")
                    .build();

            this.firebaseApp = FirebaseApp.initializeApp(options);
            this.firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
            LOG.info("Initialized firebaseApp with databaseUrl={}", firebaseApp.getOptions().getDatabaseUrl());
        } catch (IOException e) {
            LOG.error("Failed to initialize reason={}", e.getLocalizedMessage(), e);
        }
    }

    public FirebaseApp getFirebaseApp() {
        return firebaseApp;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
}
