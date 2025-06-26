package mx.ipn.escom.ProyectoFinal.services;

import mx.ipn.escom.ProyectoFinal.models.Sismo;
import java.util.List;

public interface SismoService {
    List<Sismo> obtenerTodosLosSismos();
    Sismo obtenerSismoPorId(Long id); // ✅ SOLO SE DECLARA, SIN {} !!
}
