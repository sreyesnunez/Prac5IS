package mx.ipn.escom.ProyectoFinal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.ipn.escom.ProyectoFinal.models.Sismo;
import mx.ipn.escom.ProyectoFinal.repositories.SismoRepository;
import mx.ipn.escom.ProyectoFinal.services.SismoServiceImpl;

@ExtendWith(MockitoExtension.class)
class SismoServiceImplTest {

    @Mock
    private SismoRepository sismoRepository;

    @InjectMocks
    private SismoServiceImpl sismoService;

    @Test
    void testObtenerTodosLosSismos() {
        List<Sismo> sismos = Arrays.asList(new Sismo(), new Sismo());
        when(sismoRepository.findAll()).thenReturn(sismos);

        List<Sismo> resultado = sismoService.obtenerTodosLosSismos();

        assertEquals(2, resultado.size());
        verify(sismoRepository, times(1)).findAll();
    }
    
    @Test
    void testObtenerSismoPorId() {
    // Preparación
    Long id = 1L;
    Sismo sismo = new Sismo();
    sismo.setId(id); // Asegúrate de que Sismo tenga un método setId(Long)

    // Configura el mock
    when(sismoRepository.findById(id)).thenReturn(Optional.of(sismo));

    // Ejecución
    Sismo resultado = sismoService.obtenerSismoPorId(id);

    // Verificación
    assertNotNull(resultado);
    assertEquals(id, resultado.getId());
    verify(sismoRepository, times(1)).findById(id);
    }

    @Test
    void testObtenerSismoPorIdNoEncontrado() {
    // Preparación
    Long id = 99L;
    when(sismoRepository.findById(id)).thenReturn(Optional.empty());

    // Ejecución
    Sismo resultado = sismoService.obtenerSismoPorId(id);

    // Verificación
    assertNull(resultado);
    verify(sismoRepository, times(1)).findById(id);
    }

}
