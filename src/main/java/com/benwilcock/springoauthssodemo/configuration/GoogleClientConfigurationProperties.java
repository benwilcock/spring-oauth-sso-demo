package com.benwilcock.springoauthssodemo.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Models the Google configuration elements in the application properties
 * and creates a @{@link Bean} containing them.
 */
@Component
public class GoogleClientConfigurationProperties {

    @Bean(name = "google")
    @ConfigurationProperties("google")
    public OAuthClientConfigurationProperties google() {
        return new OAuthClientConfigurationProperties();
    }
}
