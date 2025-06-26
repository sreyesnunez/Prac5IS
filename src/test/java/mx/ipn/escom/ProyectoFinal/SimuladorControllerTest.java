package mx.ipn.escom.ProyectoFinal;

import mx.ipn.escom.ProyectoFinal.Controllers.SimuladorController;
import mx.ipn.escom.ProyectoFinal.models.Localidad;
import mx.ipn.escom.ProyectoFinal.models.PreferenciaUsuario;
import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import mx.ipn.escom.ProyectoFinal.services.LocalidadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SimuladorController.class)
public class SimuladorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocalidadService localidadService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "silver@upiita.ipn.mx", roles = "USER")
    void testSimuladorUsuario() throws Exception {
        // Datos simulados
        List<Localidad> localidades = List.of(new Localidad());
        when(localidadService.obtenerTodas()).thenReturn(localidades);

        Usuario usuario = new Usuario();
        PreferenciaUsuario pref = new PreferenciaUsuario();
        pref.setTema("oscuro");
        usuario.setPreferenciaUsuario(pref);

        when(userRepository.findByEmail("silver@upiita.ipn.mx"))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/simulador"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("localidades"))
                .andExpect(model().attribute("tema", "oscuro"));
    }

    @Test
    @WithMockUser(username = "admin@upiita.ipn.mx", roles = "ADMIN")
    void testSimuladorAdmin() throws Exception {
        List<Localidad> localidades = List.of(new Localidad());
        when(localidadService.obtenerTodas()).thenReturn(localidades);

        Usuario usuario = new Usuario();
        PreferenciaUsuario pref = new PreferenciaUsuario();
        pref.setTema("claro");
        usuario.setPreferenciaUsuario(pref);

        when(userRepository.findByEmail("admin@upiita.ipn.mx"))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/admin/simulador"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("localidades"))
                .andExpect(model().attribute("tema", "claro"));
    }
}
