package com.benwilcock.springoauthssodemo;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;

@Configuration
public class FilterConfiguration {

    /**
     * we need to explicitly support the redirects from our app to Github.
     * This is handled in Spring OAuth2 with a servlet Filter, and the
     * filter is already available in the application context because
     * we used @EnableOAuth2Client.
     *
     * All that is needed is to wire the filter up so that it gets called
     * in the right order in our Spring Boot application. To do that we
     * need a FilterRegistrationBean:
     * */

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }
}
