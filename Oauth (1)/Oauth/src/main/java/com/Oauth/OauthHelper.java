package com.Oauth;

import java.io.IOException;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OauthHelper {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String userInfoUri;

//     @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri="http://localhost:4200/callback";

    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String authorizationUri;

    private final CustomOauthUserService customOAuthUserService;

    public final ClientRegistrationRepository clientRegistrationRepository;

    public final UserService userService;
    

    public OidcUser processOidcUser(String authorizationCode, String clientRegistrationId) throws IOException {
       try{

               ClientRegistration clientRegistration = clientRegistrationRepository
                       .findByRegistrationId(clientRegistrationId);
               // Exchange authCode for token
               GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                       new NetHttpTransport(), new GsonFactory(),
                       clientId,
                       clientSecret,
                       authorizationCode,
                       redirectUri).execute();
               // Extract Tokens
               String idTokenValue = tokenResponse.getIdToken();
       
               String accessTokenValue = tokenResponse.getAccessToken();
          
       
               // Create Oauth2 access token
               OAuth2AccessToken accessToken = new OAuth2AccessToken(
                       OAuth2AccessToken.TokenType.BEARER,
                       accessTokenValue,
                       null,
                       null);
       
       
               // Fetch User attributes
               Map<String, Object> userAttributes = fetchUserAttributes(accessToken);
               System.out.println("\n ATTRIIIII: " + userAttributes);

               User user = userService.findByEmail(userAttributes.get("email").toString());
               if (user == null) {
            
                        user = new User();
                        user.setEmail(userAttributes.get("email").toString());
                        user.setName(userAttributes.get("name").toString());
                        user.setImage(userAttributes.get("picture").toString());
                      
                        System.out.println("\nUSER2 : " + user);
                        user = userService.registerNewUser(user);
                }
       
               // if (user == null) {
               //     return null;
       
               // }
       
               // Create OIDCToken
               OidcIdToken oidcIdToken = new OidcIdToken(idTokenValue, new Date().toInstant(),
                       Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds()), userAttributes);
                       
       
               // Create OIDCUserRequest
             
               OidcUserRequest oidcUserRequest = new OidcUserRequest(clientRegistration, accessToken, oidcIdToken,
                       userAttributes);
               return customOAuthUserService.loadUser(oidcUserRequest);
       }
       catch(Exception e){
        System.out.println("\nERRORRRRR: " + e.getMessage());
        return null;
       }
    }

    public Map<String, Object> fetchUserAttributes(OAuth2AccessToken accessToken) {
        //Create RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        //Setup Auth headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue());

        //Wrap headers in HttpEntity
        HttpEntity entity = new HttpEntity<>(headers);

        //Make Http Get request
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {
                });

        return response.getBody();

    }

}
