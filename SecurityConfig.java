package com.eventos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    public static final String[] URLS_PUBLICAS = {
        "/", "/index", "/webjars/**", "/css/**", "/js/**",
        "/login", "/acceso-denegado", "/registro/**"
    };

    // Solo admin puede gestionar usuarios y roles
    public static final String[] URLS_ADMIN = {
        "/usuario/**", "/rol/**"
    };

    // admin y organ pueden gestionar eventos
    public static final String[] URLS_ADMIN_ORGANIZADOR = {
        "/evento/guardar", "/evento/eliminar", "/evento/modificar/**", "/evento/nuevo"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(URLS_PUBLICAS).permitAll()
            .requestMatchers(URLS_ADMIN).hasRole("ADMIN")
            .requestMatchers(URLS_ADMIN_ORGANIZADOR).hasAnyRole("ADMIN", "ORGANIZADOR")
            .requestMatchers("/evento/listado").hasAnyRole("ADMIN", "ORGANIZADOR", "CLIENTE")
            .requestMatchers("/consultas/**").hasAnyRole("ADMIN", "ORGANIZADOR", "CLIENTE")
            .anyRequest().authenticated()
        ).formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .successHandler(redireccionPorRol())
            .failureUrl("/login?error=true")
            .permitAll()
        ).logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        ).exceptionHandling(ex -> ex
            .accessDeniedPage("/acceso-denegado")
        );
        return http.build();
    }

    // redireccionar basado en el rol de cada mente
    @Bean
    public AuthenticationSuccessHandler redireccionPorRol() {
        return (request, response, authentication) -> {
            boolean esAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean esOrganizador = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZADOR"));

            if (esAdmin) {
                response.sendRedirect("/usuario/listado");
            } else if (esOrganizador) {
                response.sendRedirect("/evento/listado");
            } else {
                response.sendRedirect("/evento/listado");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    @Autowired
    public void configurarAutenticacion(AuthenticationManagerBuilder builder,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy UserDetailsService userDetailsService) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
