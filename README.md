# SpringBoot OAuth SSO Demo

Hey, thanks for stopping by. This demo is based on the (excellent) Spring Tutorial "Spring Boot and OAuth2" 
which is available here: https://spring.io/guides/tutorials/spring-boot-oauth2/

This basic OAuth authentication project shows how to integrate Github and Google as OAuth providers into your application using spring-security-oauth2 project library.

## Before you start

Depending which branch you take, you'll need to...

1. You will need your own user account to test with on Github (and possibly Google depending on the branch)
2. You will need to register an OAuth application with Github (and possibly Google) and retrieve ClientID and ClientSecret
3. You will need Maven and Java
4. You will need to test this app on http://localhost:8080

## Branch: master

Illustrates the simplest and smallest amount of configuration and code possible to integrate an OAuth provider using spring-security-oauth2. It integrates with a single SSO provider (Github) using all of Spring Boot's auto-configuration magic (so very little code is required).

## Branch: more-manual-sso-configuration

This is essentially the same as the master branch, but the auto-configuration magic of SpringBoot has been stripped away somewhat in favour of a more manual approach. This mostly involved building the OAuth client configurations manually in the WebSecurityConfigurerAdapterImpl class and adding some additional classes to help with loading this configuration. 

## Branch: multiple-oauth-providers

This builds upon the 'more-manual-sso-configuration' approach and adds support for an additional OAuth provider in the shape of Google.

## Branch: cloudfoundry-uaa-provider

This branch uses a custom self-hosted OAuth provider - the [CloudFoundry UAA](https://github.com/cloudfoundry/uaa).

This branch needs a little more setup than the others, but mimics what might happen in an enterprise setting where the user accounts themselves are stored and managed by something like LDAP but are exposed in CloudFoundry using the User Authentication and Authorisation service - the UAA. 

In an enterprise setting you would use the PCF Ops Manager tile to integrate your LDAP, SAML or other user store, and then developers would use the PCF Marketplace's `single-sign-on` service to register their applications as OAuth applications. 

In this simple demo, I'm just hosting and using the UAA component directly as I don't have a PCF foundation available. This approach is still pretty simple, but it does mean building and hosting the UAA server and registering an application with it before we can test the integration. 

### Step 1: Get the UAA server code, build it and push it...

Issue the following commands to...
 
1. download the UAA code 
2. build and package the UAA server (the UAA is a Java Web Application packaged as a WAR)
3. push the UAA Server to your PCF space (I've assumed you have the CF cli and have targetted your server, org and space already)

```bash
git clone git://github.com/cloudfoundry/uaa.git
cd uaa
./gradlew clean assemble manifests -Dapp=<your-preferred-app-name-for-the-uaa> -Dapp-domain=cfapps.io # I'm pushing to Pivotal Web Services, which uses this domain.
cf push -f build/sample-manifests/uaa-cf-application.yml
```

### Step 2: Get the UAA command line client (UAAC)

The vanilla UAA contains some demo app registrations and some users already, but I like to add one of my own. The UAA has no user interface (unlike the PCF marketplace's `single-sign-on` service) so instead I'm using the uaa's own [command line client](https://github.com/cloudfoundry/cf-uaac) called `uaac`. To do this, issue the following commands to...

1. Install the Ruby gem for the `uaac` client
2. Target your UAA server with your `uaac` client
3. Get the client token for the UAA Administrator

```bash
gem install cf-uaac
uaac target https://bw-sso-server.cfapps.io
uaac token client get admin -s adminsecret
```

### Step 3: Register our demo application as an OAuth application with the UAA

Now you're logged in as the admin client in the UAA. As admin you're free to register new applications as OAuth clients. The `uaac` client offers an interactive mechanism to do this, so you don't need to have all the settings ready before you start.

```bash
uaac client add -i
Client ID:  spring-oauth-sso-demo
New client secret:  *************
Verify new client secret:  *************
scope (list):  openid oauth.approvals scim.userids
authorized grant types (list):  client_credentials authorization_code refresh_token
authorities (list):  clients.read emails.write scim.userids password.write idps.write notifications.write oauth.login scim.write critical_notifications.write
access token validity (seconds):  3600
refresh token validity (seconds):  3600
redirect uri (list):  http://localhost:8080/login/uaa http://localhost:8080
autoapprove (list):  openid
signup redirect url (url):
```

This will create for you the necessary OAuth client registration. You will need to change the properties in the `application.yml` file to target your UAA's endpoints rather than mine (unless my server is still up).

To see a list of the registered clients at any time, issue the command `uaac clients`.

> When testing the application, when you're redirected and asked to login use the default username is `marissa` and the password for this user is `koala`. You can add more users if you like using the UAAC. 

## Running the demo

### Step 1: Get the code:

```bash
$ git clone https://github.com/benwilcock/spring-oauth-sso-demo.git
$ git checkout <your-chosen-branch-name>
```

### Step 2: Add your ClientID's and ClientSecrets 

I've not added my ID's and Secrets to Git, so you'll need to add yours. In the `src/main/resources/application.yml` file add your real `clientId` and `clientSecret` where I've left placeholders for you...

```yaml
uaa:
  client:
    clientId: <your-client-id>
    clientSecret: <your-client-secret>
```

### Step 3: Run the demo application locally

To run the demo, do the following from the terminal:-

```bash
$ mvn clean package spring-boot:run
```

### Step 4: Try and login

Once your server has started successfully, begin a new 'Inconito' session in your web browser and point it to `http://localhost:8080`. 

You should see a 'Login' screen (offering a choice of providers depending on which branch you choose).

Proceed to login. You should be directed to the login screen for the appropriate provider (Github, Google or CloudFoundry depending on the branch you checked out). You may be asked to allow the demo application to access your profile (choose to allow if you want to continue). 

Once you have logged in (using two-factor authentication if you have that enabled on your account) you will be redirected back to the app and the "Logout" button and the name you registered with the provider should be displayed in the browser window.
