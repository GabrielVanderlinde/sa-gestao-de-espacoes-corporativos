package com.senai.gestao_de_espacos_corporativos.repositories;

import com.senai.gestao_de_espacos_corporativos.entities.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecursoRepository extends JpaRepository<RecursoEntity, Long> {


}
