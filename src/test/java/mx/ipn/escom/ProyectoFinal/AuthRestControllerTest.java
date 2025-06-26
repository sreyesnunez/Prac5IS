package mx.ipn.escom.ProyectoFinal;


import mx.ipn.escom.ProyectoFinal.Controllers.AuthRestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthRestController.class)
@Import(AuthRestControllerTest.NoSecurityConfig.class)
public class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    // ✅ Test de login exitoso
    @Test
    void testLoginExitoso() throws Exception {
        String json = """
            {
              "email": "silver@example.com",
              "password": "clave123"
            }
        """;

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("silver@example.com");
        when(mockAuth.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inicio de sesión exitoso"))
                .andExpect(jsonPath("$.user").value("silver@example.com"));
    }

    // ❌ Test de login fallido por credenciales inválidas
    @Test
    void testLoginFallido() throws Exception {
        String json = """
            {
              "email": "fallo@example.com",
              "password": "malclave"
            }
        """;

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    // 🔐 Configuración que desactiva seguridad en pruebas
    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        public SecurityFilterChain noSecurity(HttpSecurity http) throws Exception {
            http.csrf().disable()
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }
}
