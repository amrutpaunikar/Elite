package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.security.OAuthSuccessHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private OAuthSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 🔥 MUST use lambda — old .disable() is NOT allowed
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // disable CORS for now (you can enable later)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(
                    
                    auth -> auth
                    .requestMatchers("/signup", "/login", "/search", "/actuator/**").permitAll()
                    .requestMatchers("/auth/forgot-password",
                                    "/auth/verify-otp",
                                    "/auth/reset-password").permitAll()
                    .requestMatchers("/googlelogin", "/oauth2/**").permitAll()
                    .requestMatchers("/logout-google").authenticated()
                    .requestMatchers("/admin/**").authenticated()
                    .requestMatchers("/home").authenticated()
                    .requestMatchers("/categories","/categories/allCategories","/categories/{id}","/categories/delete-all","/categories/bulk").authenticated()
                    .requestMatchers("/ratings/bulk","/ratings/add","/ratings/allRatings","/ratings/{id}","/ratings/update/{id}","/ratings/delete/{id}").authenticated()
                    .requestMatchers("/locations/add","/locations/bulk","/locations/all","/locations/{id}","/locations/update/{id}","/locations/delete/{id}").authenticated()
                    .anyRequest().authenticated()
            )
                .oauth2Login(oauth -> oauth
                    .loginPage("/googlelogin")
                    .successHandler(successHandler))
                .logout(logout-> logout
                    .logoutUrl("/logout-google")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // Allow your frontend
    config.addAllowedOrigin("http://localhost:3000");         // local React
    config.addAllowedOrigin("https://your-frontend.com");     // production frontend
    config.addAllowedOrigin("https://your-frontends.com"); 
    config.addAllowedOriginPattern("http://localhost:5173"); // if you want all origins

    // Allow headers
    config.addAllowedHeader("*");

    // Allow methods
    config.addAllowedMethod("*");

    // Allow cookies/authorization headers
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
