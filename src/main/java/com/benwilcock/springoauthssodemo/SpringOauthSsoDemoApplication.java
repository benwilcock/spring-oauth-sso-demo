package com.benwilcock.springoauthssodemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Based on the Spring Tutorial "Spring Boot and OAuth2"
 * available here: https://spring.io/guides/tutorials/spring-boot-oauth2/
 *
 * This basic authentication sample shows how to integrate Github and Google
 * as OAuth providers into your code.
 *
 * To get started, you'll need to setup some client ID's and secrets with
 * those two providers and add them to the application.yml file.
 */
@SpringBootApplication
public class SpringOauthSsoDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringOauthSsoDemoApplication.class, args);
	}
}
