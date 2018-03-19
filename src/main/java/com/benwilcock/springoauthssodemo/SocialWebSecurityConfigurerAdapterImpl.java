package com.benwilcock.springoauthssodemo;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;

@EnableOAuth2Client
@Component
public class SocialWebSecurityConfigurerAdapterImpl extends WebSecurityConfigurerAdapter {

    private OAuth2ClientContext oAuth2ClientContext;
    private AuthorizationCodeResourceDetails github;
    private ResourceServerProperties githubResource;

    public SocialWebSecurityConfigurerAdapterImpl(OAuth2ClientContext oAuth2ClientContext, AuthorizationCodeResourceDetails github, ResourceServerProperties githubResource) {
        this.oAuth2ClientContext = oAuth2ClientContext;
        this.github = github;
        this.githubResource = githubResource;
    }

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
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                    .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);

    }

    private Filter ssoFilter(){
        OAuth2ClientAuthenticationProcessingFilter githubFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/github");
        OAuth2RestTemplate githubTemplate = new OAuth2RestTemplate(github, oAuth2ClientContext);
        githubFilter.setRestTemplate(githubTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(githubResource.getUserInfoUri(), github.getClientId());
        tokenServices.setRestTemplate(githubTemplate);
        githubFilter.setTokenServices(tokenServices);
        return githubFilter;
    }
}
