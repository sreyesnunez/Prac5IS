package mx.ipn.escom.ProyectoFinal.services;

import mx.ipn.escom.ProyectoFinal.models.Localidad;
import mx.ipn.escom.ProyectoFinal.repositories.LocalidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalidadServiceImpl implements LocalidadService {

    @Autowired
    private LocalidadRepository localidadRepository;

    @Override
    public List<Localidad> obtenerTodas() {
        return localidadRepository.findAll();
    }
}
