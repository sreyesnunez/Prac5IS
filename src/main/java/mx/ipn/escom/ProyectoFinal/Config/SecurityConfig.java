package mx.ipn.escom.ProyectoFinal.Config;

import mx.ipn.escom.ProyectoFinal.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserService userService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(authProvider));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("⚡ Configuración de seguridad activada...");

        http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/usuarios/**").hasAuthority("ROLE_ADMIN") // API CRUD
            .requestMatchers("/admin", "/admin/**").hasAuthority("ROLE_ADMIN") // Panel admin y CRUD
            .requestMatchers("/usuario/**").hasAuthority("ROLE_USER")
            .requestMatchers("/mapa/descargar-pdf/**").authenticated() 
            .requestMatchers("/simulador").hasAuthority("ROLE_USER")
            .requestMatchers("/admin/simulador").hasAuthority("ROLE_ADMIN")
            .anyRequest().permitAll()
        )
        
        .httpBasic(Customizer.withDefaults())
        .formLogin(login -> login
            .loginPage("/login")
            .loginProcessingUrl("/perform_login")
            .defaultSuccessUrl("/redirect", true)
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        )
        .sessionManagement(session -> session
            .sessionFixation().migrateSession() // Protege contra secuestro de sesión
            .maximumSessions(1) // Solo una sesión activa por usuario
            .expiredUrl("/login?expired") // Redirige si la sesión expira
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Crea sesión solo si es necesario
        )
        .csrf(csrf -> csrf.disable()); // 🔥 Deshabilita CSRF si usas APIs POST/PUT en front-end.

        return http.build();
    }

    // Manejo de eventos de sesión (para invalidar sesión si el usuario se autentica en otro lado)
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
