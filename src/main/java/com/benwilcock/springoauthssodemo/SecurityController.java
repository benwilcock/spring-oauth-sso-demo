package com.benwilcock.springoauthssodemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class SecurityController {

//    @GetMapping({ "/user", "/me" })
//    public Map<String, String> user(Principal principal) {
//        Map<String, String> map = new LinkedHashMap<>();
//        map.put("name", principal.getName());
//        return map;
//    }

    @GetMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }
}
