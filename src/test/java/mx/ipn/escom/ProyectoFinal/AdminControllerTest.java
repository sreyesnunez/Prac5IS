package mx.ipn.escom.ProyectoFinal;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.ipn.escom.ProyectoFinal.Controllers.AdminController;
import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarUsuariosApi() throws Exception {
        List<Usuario> usuarios = Arrays.asList(new Usuario(), new Usuario());
        when(userService.obtenerTodosLosUsuarios()).thenReturn(usuarios);

        mockMvc.perform(get("/admin/api/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testObtenerUsuarioPorId_Existe() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(userService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/admin/api/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testObtenerUsuarioPorId_NoExiste() throws Exception {
        when(userService.obtenerUsuarioPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/api/usuarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAgregarUsuario_Exitoso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("nuevo@correo.com");
        when(userService.emailExistente("nuevo@correo.com")).thenReturn(false);
        when(userService.guardarUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/admin/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAgregarUsuario_EmailExistente() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("existente@correo.com");
        when(userService.emailExistente("existente@correo.com")).thenReturn(true);

        mockMvc.perform(post("/admin/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarUsuario_Exitoso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("editado@correo.com");
        when(userService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(userService.guardarUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/admin/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarUsuario_NoExiste() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(999L);
        usuario.setEmail("desconocido@correo.com");
        when(userService.obtenerUsuarioPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/admin/api/usuarios/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEliminarUsuarioApi_Exitoso() throws Exception {
        when(userService.existePorId(1L)).thenReturn(true);
        doNothing().when(userService).eliminarUsuarioPorId(1L);

        mockMvc.perform(delete("/admin/api/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEliminarUsuarioApi_NoExiste() throws Exception {
        when(userService.existePorId(999L)).thenReturn(false);

        mockMvc.perform(delete("/admin/api/usuarios/999"))
                .andExpect(status().isNotFound());
    }
}
