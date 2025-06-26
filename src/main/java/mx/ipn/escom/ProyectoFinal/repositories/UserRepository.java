package mx.ipn.escom.ProyectoFinal.repositories;

import mx.ipn.escom.ProyectoFinal.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM usuarios WHERE id = :id", nativeQuery = true)
    void eliminarUsuarioTotalmente(@Param("id") Long id);
}
