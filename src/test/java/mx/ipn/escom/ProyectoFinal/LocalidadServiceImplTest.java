package mx.ipn.escom.ProyectoFinal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.ipn.escom.ProyectoFinal.models.Localidad;
import mx.ipn.escom.ProyectoFinal.repositories.LocalidadRepository;
import mx.ipn.escom.ProyectoFinal.services.LocalidadServiceImpl;

@ExtendWith(MockitoExtension.class)
class LocalidadServiceImplTest {

    @Mock
    private LocalidadRepository localidadRepository;

    @InjectMocks
    private LocalidadServiceImpl localidadService;

    @Test
    void testObtenerTodas() {
        List<Localidad> lista = Arrays.asList(new Localidad(), new Localidad());
        when(localidadRepository.findAll()).thenReturn(lista);

        List<Localidad> resultado = localidadService.obtenerTodas();

        assertEquals(2, resultado.size());
        verify(localidadRepository, times(1)).findAll();
    }
}
