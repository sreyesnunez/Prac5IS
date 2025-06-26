package mx.ipn.escom.ProyectoFinal;

import mx.ipn.escom.ProyectoFinal.Controllers.VistaSismoController;
import mx.ipn.escom.ProyectoFinal.models.Sismo;
import mx.ipn.escom.ProyectoFinal.services.SismoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class VistaSismoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SismoService sismoService;

    @InjectMocks
    private VistaSismoController vistaSismoController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // ViewResolver ficticio para evitar error de "Circular view path"
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders
                .standaloneSetup(vistaSismoController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testMostrarMapa() throws Exception {
        when(sismoService.obtenerTodosLosSismos()).thenReturn(Collections.singletonList(new Sismo()));

        mockMvc.perform(get("/mapa"))
                .andExpect(status().isOk())
                .andExpect(view().name("mapa"))
                .andExpect(model().attributeExists("sismos"));
    }
}
