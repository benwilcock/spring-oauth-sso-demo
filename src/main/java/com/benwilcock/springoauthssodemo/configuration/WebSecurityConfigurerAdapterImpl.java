package com.benwilcock.springoauthssodemo.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * In a simple single OAuth provider scenario, you don't actually need this class.
 *
 * We overide the {@link WebSecurityConfigurerAdapter} because we want to have multiple
 * providers at the same time and show what is happening in terms of setup and
 * configuration which you don't see in the regular Auto Configuration in SoringBoot.
 */
@EnableOAuth2Client
@Component
public class WebSecurityConfigurerAdapterImpl extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfigurerAdapterImpl.class);

    private OAuth2ClientContext oAuth2ClientContext;
    private OAuthClientConfigurationProperties github;
    private OAuthClientConfigurationProperties google;
    private OAuthClientConfigurationProperties uaa;

    public WebSecurityConfigurerAdapterImpl(OAuth2ClientContext oAuth2ClientContext,
                                            @Qualifier("github") OAuthClientConfigurationProperties github,
                                            @Qualifier("google") OAuthClientConfigurationProperties google,
                                            @Qualifier("uaa") OAuthClientConfigurationProperties uaa
                                            ) {

        this.oAuth2ClientContext = oAuth2ClientContext;
        this.github = github;
        this.google = google;
        this.uaa = uaa;
        this.logTheConfig("Github", github);
        this.logTheConfig("Google", google);
        this.logTheConfig("CloudFoundry Uaa", uaa);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOGGER.info("Configuring our HttpSecurity...");
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
                    .exceptionHandling()
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
                .and()
                    .logout()
                    .logoutSuccessUrl("/")
                    .permitAll()
                .and()
                    .formLogin()
                    .loginPage("/index.html")
                .and()
                    // ...and enable CSRF support using a Cookies strategy...
                    .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                    // ...and ensure our filters are constructed and used before other filters.
                    .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    /**
     * This helper method builds a {@link CompositeFilter} list containing
     * {@link Filter} objects for our two OAuth providers (Google and GitHub).
     * @return
     */
    private Filter ssoFilter() {
        String googlePath = "/login/google";
        String githubPath = "/login/github";
        String uaaPath = "/login/uaa";

        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        LOGGER.info("Creating the Servlet Filter for Google on {}...", googlePath);
        filters.add(ssoFilter(google, googlePath));
        LOGGER.info("Creating the Servlet Filter for Github on {}...", githubPath);
        filters.add(ssoFilter(github, githubPath));
        LOGGER.info("Creating the Servlet Filter for CloudFoundry Uaa on {}...", uaaPath);
        filters.add(ssoFilter(uaa, uaaPath));

        filter.setFilters(filters);
        return filter;
    }

    /**
     * This helper method is used to build {@link OAuth2ClientAuthenticationProcessingFilter} objects
     * based on the configuration properties and the filter path given.
     * @param client {@link OAuthClientConfigurationProperties}
     * @param path
     * @return
     */
    private Filter ssoFilter(OAuthClientConfigurationProperties client, String path) {
        LOGGER.info("Builing the OAuth2ClientAuthenticationProcessingFilter for the path: {}", path);
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        filter.setTokenServices(tokenServices);
        return filter;
    }

    private void logTheConfig(String name, OAuthClientConfigurationProperties client){
        LOGGER.debug("Using the OAuth configuration for {} as follows...", name);
        LOGGER.debug("User info uri: {}", client.getResource().getUserInfoUri());
        LOGGER.debug("Access token uri: {}", client.getClient().getAccessTokenUri());
        LOGGER.debug("Authentication scheme: {}", client.getClient().getAuthenticationScheme());
        LOGGER.debug("Client authentication scheme: {}", client.getClient().getClientAuthenticationScheme());
        LOGGER.debug("Grant type: {}", client.getClient().getGrantType());
    }
}
