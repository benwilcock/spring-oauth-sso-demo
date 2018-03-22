package com.benwilcock.springoauthssodemo.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Models the CloudFoundry UAA configuration elements in the application properties
 * and creates a @{@link Bean} containing them.
 */
@Component
public class UaaClientConfigurationProperties {

    @Bean(name = "uaa")
    @ConfigurationProperties("uaa")
    public OAuthClientConfigurationProperties uaa() {
        return new OAuthClientConfigurationProperties();
    }

}
