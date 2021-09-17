package com.devsuperior.dscatalog.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tb_funcionarios")
public class FuncionarioEntity {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Getter
    @Setter
    private String nome;

    @Getter
    @Setter
    private String sexo;

    @Getter
    @Setter
    private String telefone;

    @Getter
    @Setter
    @ManyToOne
    private CargoEntity cargo;
}