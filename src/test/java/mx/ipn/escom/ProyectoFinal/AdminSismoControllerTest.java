package mx.ipn.escom.ProyectoFinal;

import mx.ipn.escom.ProyectoFinal.Controllers.AdminSismoController;
import mx.ipn.escom.ProyectoFinal.Utils.PdfGenerator;
import mx.ipn.escom.ProyectoFinal.models.PreferenciaUsuario;
import mx.ipn.escom.ProyectoFinal.models.Sismo;
import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.repositories.SismoRepository;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.mockito.Mockito.mockStatic;

@WebMvcTest(AdminSismoController.class)
public class AdminSismoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SismoRepository sismoRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin@test.com")
    void testListarSismos() throws Exception {
        Sismo sismo = new Sismo();
        when(sismoRepository.findAll()).thenReturn(List.of(sismo));

        Usuario usuario = new Usuario();
        PreferenciaUsuario pref = new PreferenciaUsuario();
        pref.setTema("oscuro");
        usuario.setPreferenciaUsuario(pref);
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/admin/sismos-crud"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("sismos"))
                .andExpect(view().name("sismos_admin_crud"));
    }

    @Test
    @WithMockUser(username = "admin@test.com")
    void testMostrarFormularioAgregar() throws Exception {
        Usuario usuario = new Usuario();
        PreferenciaUsuario pref = new PreferenciaUsuario();
        pref.setTema("claro");
        usuario.setPreferenciaUsuario(pref);
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/admin/sismos-crud/agregar"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("sismo"))
                .andExpect(view().name("agregar_sismo"));
    }

    @Test
    @WithMockUser(username = "admin@test.com")
    void testAgregarSismo() throws Exception {
        mockMvc.perform(post("/admin/sismos-crud/agregar")
                        .with(csrf())
                        .param("nombre", "Sismo Test")
                        .param("magnitud", "5.5")
                        .param("latitud", "19.4326")
                        .param("longitud", "-99.1332")
                        .param("fecha", "2024-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sismos-crud"));
    }

    @Test
    @WithMockUser(username = "admin@test.com")
    void testMostrarFormularioEditar() throws Exception {
        Sismo sismo = new Sismo();
        sismo.setId(1L);
        when(sismoRepository.findById(1L)).thenReturn(Optional.of(sismo));

        Usuario usuario = new Usuario();
        PreferenciaUsuario pref = new PreferenciaUsuario();
        pref.setTema("oscuro");
        usuario.setPreferenciaUsuario(pref);
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/admin/sismos-crud/editar/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("sismo"))
                .andExpect(view().name("editar_sismo"));
    }

    @Test
    @WithMockUser(username = "admin@test.com")
    void testEditarSismo() throws Exception {
        mockMvc.perform(post("/admin/sismos-crud/editar")
                        .with(csrf())
                        .param("id", "1")
                        .param("nombre", "Sismo Editado")
                        .param("magnitud", "6.2")
                        .param("latitud", "18.5")
                        .param("longitud", "-100.1")
                        .param("fecha", "2024-01-02"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sismos-crud"));
    }

    @Test
    @WithMockUser(username = "admin@test.com")
    void testEliminarSismo() throws Exception {
        mockMvc.perform(get("/admin/sismos-crud/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sismos-crud"));

        verify(sismoRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(username = "admin@test.com")
    void testDescargarPdfSismoNoEncontrado() throws Exception {
        when(sismoRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/sismos-crud/mapa/descargar-pdf/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@test.com")
    void testDescargarPdfSismoExistente() throws Exception {
        // Arrange: Sismo de prueba
        Sismo sismo = new Sismo();
        sismo.setId(1L);
        when(sismoRepository.findById(1L)).thenReturn(Optional.of(sismo));

        // PDF simulado
        byte[] fakePdfBytes = "FAKE_PDF_CONTENT".getBytes();
        ByteArrayInputStream fakeStream = new ByteArrayInputStream(fakePdfBytes);

        // Mockear método estático PdfGenerator.generateSismoPdf()
        try (MockedStatic<PdfGenerator> mockedStatic = mockStatic(PdfGenerator.class)) {
            mockedStatic.when(() -> PdfGenerator.generateSismoPdf(sismo))
                        .thenReturn(fakeStream);

            // Act & Assert
            mockMvc.perform(get("/admin/sismos-crud/mapa/descargar-pdf/1"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "attachment; filename=reporte_sismo_1.pdf"))
                    .andExpect(content().contentType(APPLICATION_PDF))
                    .andExpect(content().bytes(fakePdfBytes));
        }
    }
}
