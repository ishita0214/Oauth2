package com.Oauth;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OauthHelper oauthHelper;

    @Autowired
    private UserService userService;

    @GetMapping("findByEmail/{email}")
    public User findByEmail(@PathVariable("email")String email){
        return userService.findByEmail(email);
    }

    @GetMapping("/authorization/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String redirectUrl = "https://accounts.google.com/o/oauth2/auth"
                + "?client_id=677028213828-qcu92o5vg5ckickokhnua5nft51miac6.apps.googleusercontent.com"
                + "&redirect_uri=http://localhost:4200/callback"
                + "&response_type=code"
                + "&scope=openid profile email";
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login2")
    public String login2(HttpServletResponse response) throws IOException {
        return "HELLOOOOO";
    }

    @GetMapping("/login")
     public ResponseEntity<?> oauthCallback(@RequestParam("code") String code) throws IOException {
        try {
            System.out.println("Received authorization code: " + code);
        
        OidcUser oidcUser = oauthHelper.processOidcUser(code, "google");
        if(oidcUser == null){
            System.out.println("\nWAPAS KARO");
        }
        System.out.println("\nOIDC USER: "+ oidcUser);

        // Authenticate user in Spring Security
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            oidcUser, null, oidcUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("\nAFTER AYTHENTICATION");
        // Fetch or create user in the database
        User user = userService.findByEmail(oidcUser.getEmail());
       
        System.out.println("\nUSER " + user);
        
        // Generate JWT
        String jwt = jwtUtil.generateToken(user);
        System.out.println("\nJWT " + jwt);
     
        userService.updateToken(user, jwt);

        // Return user info and JWT to the frontend
        LoginDTO loginDTO = new LoginDTO();
        
        loginDTO.setEmail(oidcUser.getEmail());
        loginDTO.setId(user.getId());
        loginDTO.setAccessToken(jwt);
        System.out.println("\n LOGIN DTO: " + loginDTO);
        return ResponseEntity.ok(loginDTO);

    } catch (Exception e) {
        return ResponseEntity.status(400).body("Error processing login: " + e.getMessage());
    }
    
}}
