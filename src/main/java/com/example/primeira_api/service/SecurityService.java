package com.example.primeira_api.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityService {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Desativa CSRF
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll()  // Permite acesso ao console H2
                        .requestMatchers("/tasks/**").hasRole("USER")  // Permite acesso ao endpoint /tasks/** para usuários com o papel USER
                )
                .httpBasic(withDefaults -> {});  // Configuração para autenticação básica

        // Configurações de segurança adicionais para o H2 Console
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("meuUsuario")
                .password("minhaSenha")
                .roles("USER")  // Define o papel USER para o usuário
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
