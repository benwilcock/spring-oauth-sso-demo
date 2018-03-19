package com.benwilcock.springoauthssodemo;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GoogleConfiguration {

    @Bean(name = "google")
    @ConfigurationProperties("google")
    public ClientResources google() {
        return new ClientResources();
    }
}
