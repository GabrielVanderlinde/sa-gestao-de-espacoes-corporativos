package com.senai.gestao_de_espacos_corporativos.repositories;

import com.senai.gestao_de_espacos_corporativos.entities.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    List<ReservaEntity> findByUsuarioId(Long usuarioId);
    List<ReservaEntity> findByRecursoId(Long recursoId);


}
