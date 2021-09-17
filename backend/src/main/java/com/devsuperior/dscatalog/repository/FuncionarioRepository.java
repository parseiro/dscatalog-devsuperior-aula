package com.devsuperior.dscatalog.repository;

import com.devsuperior.dscatalog.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
}
