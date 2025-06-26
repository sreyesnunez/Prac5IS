package mx.ipn.escom.ProyectoFinal.repositories;

import mx.ipn.escom.ProyectoFinal.models.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, Long> {
}
