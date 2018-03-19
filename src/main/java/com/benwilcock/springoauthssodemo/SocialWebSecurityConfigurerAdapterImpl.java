package com.benwilcock.springoauthssodemo;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;

@Profile("secret")
@EnableOAuth2Sso // Has to be present on this component for it to meet it's SpringBoot conditions
@Component
public class SocialWebSecurityConfigurerAdapterImpl extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // From the root '/' down...
                .antMatcher("/**")
                // requests are authorised...
                .authorizeRequests()
                    // ...to these url's...
                    .antMatchers("/", "/login**", "/webjars/**")
                    // ...without security being applied...
                    .permitAll()
                    // ...any other requests...
                    .anyRequest()
                    // ...the user must be authenticated.
                    .authenticated()
                .and()
                    .logout()
                    .logoutSuccessUrl("/")
                    .permitAll()
                .and()
                    .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }
}
