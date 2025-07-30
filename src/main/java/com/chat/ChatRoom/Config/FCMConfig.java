package com.chat.ChatRoom.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FCMConfig {

    @PostConstruct
    public void initialize(){

        Dotenv dotenv=Dotenv.load();
        String path= dotenv.get("FIREBASE_CONFIG");
        try{
            GoogleCredentials googleCredentials=GoogleCredentials
                    .fromStream(new ClassPathResource(path).getInputStream());

            FirebaseOptions options= FirebaseOptions.builder()
                    .setCredentials(googleCredentials)
                    .build();

            if(FirebaseApp.getApps().isEmpty()){
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize firebase",e);
        }
    }
}
