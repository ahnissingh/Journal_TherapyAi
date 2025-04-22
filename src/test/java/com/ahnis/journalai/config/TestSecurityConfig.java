package com.ahnis.journalai.config;

import com.ahnis.journalai.common.security.JwtUtil;
import com.ahnis.journalai.common.security.jwt.JwtAuthenticationFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableWebSecurity
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtUtil jwtUtil() {
        return mock(JwtUtil.class);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            );

        return http.build();
    }

    @Bean
    @Primary
    public MongoMappingContext mongoMappingContext() {
        return mock(MongoMappingContext.class);
    }
}
