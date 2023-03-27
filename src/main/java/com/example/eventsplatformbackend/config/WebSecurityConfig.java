package com.example.eventsplatformbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final AuthManager authManager;

    public WebSecurityConfig(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .formLogin().disable()
                .httpBasic().disable();

        http.authorizeHttpRequests(req -> {
            req.requestMatchers("/user/login", "/user/create")
                    .permitAll();
            req.requestMatchers(HttpMethod.GET, "/user/**")
                    .permitAll();
            req.requestMatchers(HttpMethod.DELETE, "/user/**")
                    .hasRole("ADMIN");
            req.requestMatchers(HttpMethod.POST, "/user/role")
                    .hasRole("ADMIN");
        });

        http.authenticationManager(authManager);

        return http.build();
    }
}
