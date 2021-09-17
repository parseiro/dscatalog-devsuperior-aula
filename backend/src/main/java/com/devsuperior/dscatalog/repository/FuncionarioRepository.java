package com.devsuperior.dscatalog.repository;

import com.devsuperior.dscatalog.entities.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<FuncionarioEntity, Long> {
}
