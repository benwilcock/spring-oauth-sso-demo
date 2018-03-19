package com.benwilcock.springoauthssodemo;

import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@EnableOAuth2Client
@Component
public class SocialWebSecurityConfigurerAdapterImpl extends WebSecurityConfigurerAdapter {

    private OAuth2ClientContext oAuth2ClientContext;
    private ClientResources github;
    private ClientResources google;

    public SocialWebSecurityConfigurerAdapterImpl(OAuth2ClientContext oAuth2ClientContext,
                                                  @Qualifier("github") ClientResources github,
                                                  @Qualifier("google") ClientResources google) {

        this.oAuth2ClientContext = oAuth2ClientContext;
        this.github = github;
        this.google = google;
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

//    private Filter ssoFilter(){
//
//        CompositeFilter filter = new CompositeFilter();
//        List<Filter> filters = new ArrayList<>();
//
//        OAuth2ClientAuthenticationProcessingFilter githubFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/github");
//        OAuth2RestTemplate githubTemplate = new OAuth2RestTemplate(github, oAuth2ClientContext);
//        githubFilter.setRestTemplate(githubTemplate);
//        UserInfoTokenServices githubTokenServices = new UserInfoTokenServices(githubResource.getUserInfoUri(), github.getClientId());
//        githubTokenServices.setRestTemplate(githubTemplate);
//        githubFilter.setTokenServices(githubTokenServices);
//        filters.add(githubFilter);
//
//        OAuth2ClientAuthenticationProcessingFilter googleFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/google");
//        OAuth2RestTemplate googleTemplate = new OAuth2RestTemplate(google, oAuth2ClientContext);
//        googleFilter.setRestTemplate(googleTemplate);
//        UserInfoTokenServices googleTokenServices = new UserInfoTokenServices(googleResource.getUserInfoUri(), google.getClientId());
//        googleTokenServices.setRestTemplate(googleTemplate);
//        googleFilter.setTokenServices(googleTokenServices);
//        filters.add(googleFilter);
//
//        filter.setFilters(filters);
//        return filter;
//    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(google, "/login/google"));
        filters.add(ssoFilter(github, "/login/github"));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        filter.setTokenServices(tokenServices);
        return filter;
    }
}
