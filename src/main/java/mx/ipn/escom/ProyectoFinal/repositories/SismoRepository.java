package mx.ipn.escom.ProyectoFinal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import mx.ipn.escom.ProyectoFinal.models.Sismo;

public interface SismoRepository extends JpaRepository<Sismo, Long> {
}
