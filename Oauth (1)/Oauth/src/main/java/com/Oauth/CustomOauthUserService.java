package com.Oauth;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOauthUserService extends OidcUserService {
    @Autowired
    private UserDao userDao;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        // Call the parent method to fetch the OIDC user
        
        OidcUser oidcUser = super.loadUser(userRequest);
        
        
        // Extract the email attribute
        String email = (String) oidcUser.getAttributes().get("email");

        // Check if the user exists in the database
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Get user authorities
        Set<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());
        

        // Return a new OIDC user with the updated authorities
        return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "email"); // The "email" claim is used as the key for authentication
    }
}
