package com.devsuperior.dscatalog.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "tb_funcionarios")
public class FuncionarioEntity {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Getter
    @Setter
    @ToString.Include
    private String name;

    @Getter
    @Setter
    @ToString.Include
    private String sexo;

    @Getter
    @Setter
    @ToString.Include
    private String telefone;

    @JoinColumn
    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @ToString.Include
    private CargoEntity cargo;
}