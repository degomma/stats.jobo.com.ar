package com.jobo.stats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuración de seguridad sin los métodos obsoletos
        http
                .authorizeRequests(authz -> authz
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/login", "/callback", "/sanityCheck", "/getToken", "/topSongs", "/topArtists")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF
                .cors(Customizer.withDefaults()) // Asegura que CORS esté habilitado
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(new Http403ForbiddenEntryPoint()) // Manejo de errores 403
                );

        return http.build();  // Usar http.build() en lugar de .and()
    }

    // Configuración global de CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Habilitar CORS para todas las rutas
                .allowedOrigins("http://localhost:3000")  // Origen de tu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "FETCH")  // Métodos permitidos
                .allowedHeaders("*")  // Permite cualquier header
                .allowCredentials(true);  // Permite el uso de credenciales (cookies, Authorization header)
    }
}