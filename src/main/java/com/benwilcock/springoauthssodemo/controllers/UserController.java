package com.benwilcock.springoauthssodemo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /**
     * It’s not a great idea to return a whole Principal in a /user endpoint
     * like this (it might contain information you would rather not reveal
     * to a browser client). There is a lot of data, and not every OAuth
     * provider carries the same data. Guthub has different data to Google
     * for example.
     *
     * We only did it to get something working quickly. Later in the guide
     * the authors convert the endpoint to hide the information we don’t
     * need the browser to have but this will impact the UI.
     *
     * The alternative user() method in the comments below provides a simple example
     * @param principal
     * @return
     */

    @GetMapping("/user")
    public Principal user(Principal principal) throws JsonProcessingException {
        LOGGER.info("Returning the Principal: '{}'", principal.getName());

        if(principal.getClass().isAssignableFrom(OAuth2Authentication.class)) {
            OAuth2Authentication oauthPrincipal = (OAuth2Authentication) principal;
            boolean isAuthenticated = oauthPrincipal.isAuthenticated();
            LOGGER.debug("Is authenticated?: {}", isAuthenticated);
            oauthPrincipal.getAuthorities().stream().forEach(
                    element -> LOGGER.debug("Granted Authority: {}", element));
        }

        ObjectMapper mapper = new ObjectMapper();
        LOGGER.debug("Principal as JSON: {}", mapper.writeValueAsString(principal));
        return principal;
    }

    /**
     * In this alternate user({@link Principal}) method, only the users Name
     * field is extracted and placed into a {@link java.util.Map}.
     *
     * If you uncomment, the UI will not display the "Logout" page because
     * it is expecting a JSON object containing the node "userAuthentication.details.name"
     * to be present. Some small refactoring of the Javascript would fix that.
     */
    /*
    @GetMapping({ "/user", "/me" })
    public Map<String, String> user(Principal principal) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", principal.getName());
        return map;
    }
    */

}
