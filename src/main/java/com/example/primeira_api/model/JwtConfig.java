package com.example.primeira_api.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {

    private static final String SECRET_KEY = "c29tZS1sb25nLXNlY3JldC1rZXktZm9yLWp3dC1hdXRoLWFwcA=="; // Base64 válida para 256 bits

    @Bean
    public JwtEncoder jwtEncoder() {
        // Decodifica a chave secreta Base64 e cria um JWK
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
        JWK jwk = new com.nimbusds.jose.jwk.OctetSequenceKey.Builder(key).build();
        JWKSource<SecurityContext> jwkSource = (jwkSelector, context) -> jwkSelector.select(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Decodifica a chave secreta Base64 e cria um decoder
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
