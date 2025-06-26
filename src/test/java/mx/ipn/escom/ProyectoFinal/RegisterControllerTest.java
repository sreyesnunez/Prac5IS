package mx.ipn.escom.ProyectoFinal;

import mx.ipn.escom.ProyectoFinal.Controllers.RegisterController;
import mx.ipn.escom.ProyectoFinal.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
@Import(RegisterControllerTest.NoSecurityConfig.class)
public class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testMostrarPaginaRegistro() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    void testRegistroExitoso() throws Exception {
        when(userService.registerUser("Silver", "silver@example.com", "secreto123"))
                .thenReturn(true);

        mockMvc.perform(post("/register")
                        .param("nombre", "Silver")
                        .param("email", "silver@example.com")
                        .param("password", "secreto123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testRegistroFallidoCorreoYaRegistrado() throws Exception {
        when(userService.registerUser("Silver", "silver@example.com", "secreto123"))
                .thenReturn(false);

        mockMvc.perform(post("/register")
                        .param("nombre", "Silver")
                        .param("email", "silver@example.com")
                        .param("password", "secreto123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("register"));
    }

    // ✅ Desactiva la seguridad solo para estas pruebas
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
