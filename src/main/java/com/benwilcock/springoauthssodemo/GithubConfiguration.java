package com.benwilcock.springoauthssodemo;


import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.stereotype.Component;

@Component
public class GithubConfiguration {

    @Bean(name = "github")
    @ConfigurationProperties("github.client")
    public AuthorizationCodeResourceDetails github() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean(name = "githubResource")
    @ConfigurationProperties("github.resource")
    public ResourceServerProperties facebookResource() {
        return new ResourceServerProperties();
    }
}
