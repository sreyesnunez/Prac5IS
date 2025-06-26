package mx.ipn.escom.ProyectoFinal;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.ipn.escom.ProyectoFinal.Controllers.AdminController;
import mx.ipn.escom.ProyectoFinal.Config.TestSecurityConfig;
import mx.ipn.escom.ProyectoFinal.models.Rol;
import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.repositories.RoleRepository;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import mx.ipn.escom.ProyectoFinal.services.LocalidadService;
import mx.ipn.escom.ProyectoFinal.services.SismoService;
import mx.ipn.escom.ProyectoFinal.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class)
@Import(TestSecurityConfig.class) // Desactiva la seguridad para estas pruebas
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean private UserRepository userRepository;
    @MockBean private RoleRepository roleRepository;
    @MockBean private PasswordEncoder passwordEncoder;
    @MockBean private UserService userService;
    @MockBean private SismoService sismoService;
    @MockBean private LocalidadService localidadService;

    @Test
    void testListarUsuariosApi() throws Exception {
        List<Usuario> usuarios = Arrays.asList(new Usuario(), new Usuario());
        when(userRepository.findAll()).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testObtenerUsuarioPorId_Existe() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerUsuarioPorId_NoExiste() throws Exception {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAgregarUsuario_Exitoso() throws Exception {
        when(userRepository.findByEmail("nuevo@correo.com")).thenReturn(Optional.empty());
        when(roleRepository.findByNombre("ROLE_USER")).thenReturn(Optional.of(new Rol()));
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Nuevo Usuario",
                                "email", "nuevo@correo.com",
                                "password", "password123",
                                "rol", "ROLE_USER"
                        ))))
                .andExpect(status().isCreated());
    }

    @Test
    void testAgregarUsuario_EmailExistente() throws Exception {
        when(userRepository.findByEmail("existente@correo.com"))
                .thenReturn(Optional.of(new Usuario()));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Ya Existe",
                                "email", "existente@correo.com",
                                "password", "pass",
                                "rol", "ROLE_USER"
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testActualizarUsuario_Exitoso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(roleRepository.findByNombre("ROLE_ADMIN")).thenReturn(Optional.of(new Rol()));

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Editado",
                                "email", "editado@correo.com",
                                "rol", "ROLE_ADMIN"
                        ))))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarUsuario_NoExiste() throws Exception {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/usuarios/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Desconocido",
                                "email", "desconocido@correo.com",
                                "rol", "ROLE_USER"
                        ))))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarUsuarioApi_Exitoso() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarUsuarioApi_NoExiste() throws Exception {
        when(userRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/usuarios/999"))
                .andExpect(status().isNotFound());
    }
}
