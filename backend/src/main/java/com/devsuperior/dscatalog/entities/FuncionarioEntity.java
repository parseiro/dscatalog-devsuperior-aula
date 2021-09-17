package com.devsuperior.dscatalog.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tb_product")
public class FuncionarioEntity {
    @Id
    @Getter
    @Setter
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