package com.sunrise.dental.config;

import com.sunrise.dental.security.JwtFilter;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }

    @Bean
    SecurityFilterChain filter(HttpSecurity h, JwtFilter jwt,
            CorsConfigurationSource corsConfigurationSource) throws Exception {
        return h.csrf(x -> x.disable())
                .cors(x -> x.configurationSource(corsConfigurationSource))
                .sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(x -> x
                        .requestMatchers("/api/auth/login", "/api/auth/signup", "/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll().requestMatchers(HttpMethod.GET, "/api/dentists", "/api/treatments").authenticated()
                        .anyRequest().authenticated())
                .exceptionHandling(x -> x.authenticationEntryPoint((q, r, e) -> {
                    r.setStatus(401);
                    r.setContentType("application/json");
                    r.getWriter().write("{\"message\":\"Please sign in to continue.\"}");
                })).addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class).build();
    }
}
