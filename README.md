# SpringBoot OAuth SSO Demo

Hey, thanks for stopping by. This demo is based on the (excellent) Spring Tutorial "Spring Boot and OAuth2" 
which is available here: https://spring.io/guides/tutorials/spring-boot-oauth2/

This basic OAuth authentication project shows how to integrate Github and Google as OAuth providers into your application using spring-security-oauth2 project library.

## Before you start

Depending which branch you take, you'll need to...

1. You will need a user account to test with on Github and Google
2. You will need to register an OAuth application with Github and Google and retrieve ClientID and ClientSecret
3. You will need Maven and you will need to test on http://localhost:8080 (depending on your resgistered redirect for 2 above)

## Branch: master

Illustrates the simplest and smallest amount of configuration and code possible. Integrates with a single SSO provider (Github) using all of Spring Boot's auto-configuration magic. Very little code required.

## Branch: more-manual-sso-configuration

This is essentially the same as the master, but the auto-configuration magic of SpringBoot has been stripped away somewhat in favour of a more manual approach. This mostly involved building the OAuth client configurations manually in the WebSecurityConfigurerAdapterImpl class and adding some additional classes to help with loading this configuration. 

## Branch: multiple-oauth-providers

This builds upon the 'more-manual-sso-configuration' approach and adds support for an additional OAuth provider in the shape of Google.

## Running the demo code.

Get the code:

```bash
$ git clone <this-repo>
$ git checkout <your-chosen-branch>
```

To run the demo, do the following from the terminal:-

```bash
$ mvn clean package spring-boot:run
```

Once your server is up and running and started successfully, start a new 'Inconito' session in your browser and point it to `http://localhost:8080`. You should see a 'Login' screen (offering a choice depending on which branch you chose).

You can proceed to login and you should be directed to the login screen for the appropriate provider (Github or Google). You may be asked to allow the app access to your profile. Choose to allow if you want to continue. 

Once you have logged in (using 2FA if you have that enabled on your account) you will be redirected back to the app and the "Logout" button and the name you registered with the provider should be displayed.
