package com.tovelop.maphant.configure.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseInitialization(): FirebaseApp {
        val firebaseOptions = FirebaseOptions.builder()
            .setCredentials(
                GoogleCredentials.fromStream(
                    ClassPathResource("firebase/serviceAccountKey.json").inputStream
                )
            )
            .build()

        return if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(firebaseOptions)
        } else {
            FirebaseApp.getInstance()
        }
    }
}
