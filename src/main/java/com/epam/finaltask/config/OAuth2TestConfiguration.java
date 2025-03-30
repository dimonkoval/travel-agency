package com.epam.finaltask.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
public class OAuth2TestConfiguration {

    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    public ClientRegistrationRepository clientRegistrationRepository() {

        ClientRegistration dummyRegistration = ClientRegistration.withRegistrationId("dummy")
                .clientId("dummy-client-id")
                .clientSecret("dummy-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://dummy/authorize")
                .tokenUri("https://dummy/token")
                .userInfoUri("https://dummy/userinfo")
                .build();

        return new InMemoryClientRegistrationRepository(dummyRegistration);
    }
}
