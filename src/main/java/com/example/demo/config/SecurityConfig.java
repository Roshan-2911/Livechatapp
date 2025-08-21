package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/index.html", "/ws/**", "/app/**", "/topic/**", "/queue/**").permitAll()
        .anyRequest().permitAll()
      )
      .httpBasic(Customizer.withDefaults())
      .formLogin(login -> login.disable());
    return http.build();
  }
}