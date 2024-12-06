package com.Oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOauthUserService customOAuthUserService;  // Custom user service for handling OAuth user info

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("HIT");
        http
            .authorizeHttpRequests(authorize -> authorize
                        // Permit access to OAuth-related endpoints
                        .requestMatchers("/login", "/login/**", "/authorization/google","/findByEmail/**" ).permitAll()
                        // Any other requests must be authenticated
                        .anyRequest().authenticated()
                )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/login", "/login/**") // Disable CSRF for specific endpoints if necessary
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(customOAuthUserService)
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}