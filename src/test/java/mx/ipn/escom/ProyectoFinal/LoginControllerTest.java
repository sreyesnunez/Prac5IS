package mx.ipn.escom.ProyectoFinal;

import mx.ipn.escom.ProyectoFinal.Controllers.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk()); // Elimina .andExpect(view().name("login"))
    }


    @Test
    void testRedirectAdmin() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(
                "admin", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(get("/redirect").with(authentication(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void testRedirectUser() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(
                "user", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(get("/redirect").with(authentication(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario"));
    }

    @Test
    void testRedirectSinRolValido() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(
                "otro", "password", List.of(new SimpleGrantedAuthority("ROLE_GUEST")));

        mockMvc.perform(get("/redirect").with(authentication(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }
}
