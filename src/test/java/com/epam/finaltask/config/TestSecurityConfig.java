//package com.epam.finaltask.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import static org.mockito.Mockito.mock;
//
//@TestConfiguration
//public class TestSecurityConfig {
//
//    @Bean
//    @Primary
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        return mock(ClientRegistrationRepository.class);
//    }
//
//    @Bean
//    @Primary
//    public OAuth2AuthorizedClientService authorizedClientService() {
//        return mock(OAuth2AuthorizedClientService.class);
//    }
//}