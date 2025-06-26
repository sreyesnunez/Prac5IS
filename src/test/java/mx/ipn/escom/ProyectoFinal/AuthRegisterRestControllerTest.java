package mx.ipn.escom.ProyectoFinal;

import mx.ipn.escom.ProyectoFinal.Controllers.AuthRegisterRestController;
import mx.ipn.escom.ProyectoFinal.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthRegisterRestController.class)
public class AuthRegisterRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    void testRegistroExitoso() throws Exception {
        String json = """
            {
              "nombre": "Silver",
              "email": "silver@example.com",
              "password": "secreto123"
            }
        """;

        when(userService.registerUser("Silver", "silver@example.com", "secreto123"))
                .thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario registrado con éxito"));
    }

    @Test
    @WithMockUser
    void testRegistroFallidoPorCorreoDuplicado() throws Exception {
        String json = """
            {
              "nombre": "Silver",
              "email": "ya@existe.com",
              "password": "password"
            }
        """;

        when(userService.registerUser("Silver", "ya@existe.com", "password"))
                .thenReturn(false);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El correo ya está registrado"));
    }
}
