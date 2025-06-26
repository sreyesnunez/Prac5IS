package mx.ipn.escom.ProyectoFinal.services;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.ipn.escom.ProyectoFinal.models.Sismo;
import mx.ipn.escom.ProyectoFinal.repositories.SismoRepository;

@Service
public class SismoServiceImpl implements SismoService {

    @Autowired
    private SismoRepository sismoRepository;

    @Override
    public List<Sismo> obtenerTodosLosSismos() {
        return sismoRepository.findAll();
    }

    @Override
    public Sismo obtenerSismoPorId(Long id) {
        return sismoRepository.findById(id).orElse(null);
    }
}
