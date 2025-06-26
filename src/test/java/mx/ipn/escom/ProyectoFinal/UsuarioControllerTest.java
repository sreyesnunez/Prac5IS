package mx.ipn.escom.ProyectoFinal;

import mx.ipn.escom.ProyectoFinal.Controllers.UsuarioController;
import mx.ipn.escom.ProyectoFinal.models.PreferenciaUsuario;
import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import mx.ipn.escom.ProyectoFinal.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebMvcTest(controllers = UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    private Usuario usuario;

    @BeforeEach
    public void setup() {
        usuario = new Usuario();
        usuario.setNombre("Ejemplo");
        usuario.setEmail("usuario@prueba.com");
        PreferenciaUsuario pref = new PreferenciaUsuario();
        pref.setTema("oscuro");
        usuario.setPreferenciaUsuario(pref);
    }

    @Test
    @WithMockUser(username = "usuario@prueba.com")
    public void testUsuarioPerfil_Exito() throws Exception {
        when(userRepository.findByEmail("usuario@prueba.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(MockMvcRequestBuilders.get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attribute("tema", "oscuro"));
    }

    @Test
    @WithMockUser(username = "usuario@prueba.com")
    public void testUsuarioPerfil_NoEncontrado() throws Exception {
        when(userRepository.findByEmail("usuario@prueba.com")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/usuario"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=user_not_found"));
    }

    @Test
    @WithMockUser(username = "usuario@prueba.com")
    public void testMostrarFormularioEdicion_Exito() throws Exception {
        when(userRepository.findByEmail("usuario@prueba.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(MockMvcRequestBuilders.get("/usuario/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil_editar"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attribute("tema", "oscuro"));
    }

    @Test
    @WithMockUser(username = "usuario@prueba.com")
    public void testMostrarFormularioEdicion_NoEncontrado() throws Exception {
        when(userRepository.findByEmail("usuario@prueba.com")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/usuario/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=user_not_found"));
    }

    @Test
    @WithMockUser(username = "usuario@prueba.com", roles = "USER")
    public void testProcesarEdicion_Exito() throws Exception {
        when(userService.actualizarUsuario(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/usuario/editar")
                        .with(csrf())
                        .param("nombre", "NuevoNombre")
                        .param("email", "usuario@prueba.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario?exito"));
    }

    @Test
    @WithMockUser(username = "usuario@prueba.com", roles = "USER")
    public void testProcesarEdicion_ErroresDeValidacion() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/usuario/editar")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil_editar"));
    }

    @Test
    @WithMockUser(username = "usuario@prueba.com", roles = "USER")
    public void testProcesarEdicion_FalloActualizacion() throws Exception {
        when(userService.actualizarUsuario(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/usuario/editar")
                        .with(csrf())
                        .param("nombre", "NuevoNombre")
                        .param("email", "usuario@prueba.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil_editar"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "usuario@prueba.com", roles = "USER")
    public void testCambiarTemaUsuario() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/usuario/cambiar-tema")
                        .with(csrf())
                        .param("tema", "oscuro"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuario"));
    }
}
