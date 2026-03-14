package com.example.unitedservice.security;

import nl.martijndwars.webpush.PushService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;
import java.security.Security;

@Configuration
public class PushNotificationConfig {

    @Value("${vapid.public.key}")
    private String vapidPublicKey;

    @Value("${vapid.private.key}")
    private String vapidPrivateKey;

    @Value("${vapid.subject:mailto:admin@unitedservice.com}")
    private String vapidSubject;

    @Bean
    public PushService pushService() throws GeneralSecurityException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        return new PushService(vapidPublicKey, vapidPrivateKey, vapidSubject);
    }
}