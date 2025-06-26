package mx.ipn.escom.ProyectoFinal;

import mx.ipn.escom.ProyectoFinal.Controllers.SismoController;
import mx.ipn.escom.ProyectoFinal.models.Localidad;
import mx.ipn.escom.ProyectoFinal.models.PreferenciaUsuario;
import mx.ipn.escom.ProyectoFinal.models.Sismo;
import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import mx.ipn.escom.ProyectoFinal.services.LocalidadService;
import mx.ipn.escom.ProyectoFinal.services.SismoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.RequestPostProcessor;



import java.util.Collections;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SismoController.class)
public class SismoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SismoService sismoService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private LocalidadService localidadService;

    private Usuario usuario;
    private Sismo sismo;

    private final RequestPostProcessor mockUser = user("test@example.com").roles("USER");

    @BeforeEach
    public void setUp() {
        PreferenciaUsuario preferencia = new PreferenciaUsuario();
        preferencia.setTema("oscuro");

        usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setPreferenciaUsuario(preferencia);

        sismo = new Sismo();
        sismo.setZona("CDMX");
        sismo.setFecha("2023-01-01");
        sismo.setHora("12:00:00");
        sismo.setMagnitud(6.0);
        sismo.setProfundidad(10.0);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(localidadService.obtenerTodas()).thenReturn(Collections.singletonList(new Localidad()));
        when(sismoService.obtenerTodosLosSismos()).thenReturn(Collections.singletonList(sismo));
    }

    @Test
    public void testMostrarSismosSinFiltros() throws Exception {
        mockMvc.perform(get("/sismos").with(mockUser))
                .andExpect(status().isOk())
                .andExpect(view().name("sismos"))
                .andExpect(model().attributeExists("sismos"))
                .andExpect(model().attribute("tema", "oscuro"))
                .andExpect(model().attributeExists("localidades"));
    }

    @Test
    public void testFiltroZona() throws Exception {
        mockMvc.perform(get("/sismos").param("zona", "CDMX").with(mockUser))
                .andExpect(status().isOk())
                .andExpect(view().name("sismos"));
    }

    @Test
    public void testFiltroFecha() throws Exception {
        mockMvc.perform(get("/sismos").param("fecha", "2023-01-01").with(mockUser))
                .andExpect(status().isOk())
                .andExpect(view().name("sismos"));
    }

    @Test
    public void testFiltroHora() throws Exception {
        mockMvc.perform(get("/sismos").param("hora", "12:00").with(mockUser))
                .andExpect(status().isOk())
                .andExpect(view().name("sismos"));
    }

    @Test
    public void testFiltroMagnitud() throws Exception {
        mockMvc.perform(get("/sismos").param("magnitud", "5.5").with(mockUser))
                .andExpect(status().isOk())
                .andExpect(view().name("sismos"));
    }

    @Test
    public void testFiltroProfundidad() throws Exception {
        mockMvc.perform(get("/sismos").param("profundidad", "15.0").with(mockUser))
                .andExpect(status().isOk())
                .andExpect(view().name("sismos"));
    }
}
