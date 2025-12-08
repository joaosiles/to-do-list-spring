package com.todo_list.todolist.user.security;

import com.todo_list.todolist.filter.FilterTaskAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private FilterTaskAuth filterTaskAuth;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/").permitAll()
                        .requestMatchers("/users/login").permitAll()
                        .requestMatchers("/tasks/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(filterTaskAuth, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}