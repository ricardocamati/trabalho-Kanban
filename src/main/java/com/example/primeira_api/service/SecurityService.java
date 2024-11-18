package com.example.primeira_api.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityService {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("meuUsuario")
                        .password("{noop}minhaSenha") // Senha não codificada para testes
                        .roles("USER")
                        .build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Desativa CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll() // Permitir acesso ao login
                        .requestMatchers(HttpMethod.POST, "/tasks").authenticated() // Permitir criação de tarefas
                        .requestMatchers(HttpMethod.PUT, "/tasks/**").authenticated() // Permitir atualizações
                        .requestMatchers(HttpMethod.DELETE, "/tasks/**").authenticated() // Permitir exclusão
                        .requestMatchers(HttpMethod.GET, "/tasks").authenticated() // Permitir listagem
                        .anyRequest().authenticated() // Outras requisições exigem autenticação
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configuração sem estado
                .addFilterBefore(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // Filtro JWT personalizado

        return http.build();
    }


    private static class JwtTokenFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String decoded;
                try {
                    decoded = new String(Base64.getDecoder().decode(token));
                } catch (IllegalArgumentException e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token format");
                    return;
                }

                if (decoded.contains("username=") && decoded.contains("expiresAt=")) {
                    String username = decoded.split("&")[0].split("=")[1];
                    String expiresAt = decoded.split("&")[1].split("=")[1];

                    System.out.println("Decoded Token Username: " + username);
                    System.out.println("Token Expiration: " + expiresAt);

                    if (Instant.parse(expiresAt).isAfter(Instant.now())) {
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(username, null, null));
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Token expired");
                        return;
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token content");
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }
    }


}
