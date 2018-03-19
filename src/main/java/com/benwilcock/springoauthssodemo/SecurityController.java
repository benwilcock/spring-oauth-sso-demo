package com.benwilcock.springoauthssodemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class SecurityController {

    /**
     * Note:
     * It’s not a great idea to return a whole Principal in a /user endpoint like this
     * (it might contain information you would rather not reveal to a browser client).
     * We only did it to get something working quickly. Later in the guide we will convert
     * the endpoint to hide the information we don’t need the browser to have.
     * */
    @GetMapping("/user")
    public Principal user(Principal principal){
        return principal;
    }
}
