package com.devsuperior.dscatalog.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "tb_cargos")
@Entity
public class Cargo implements Serializable {
    @Getter
    @Setter
    @ToString.Include
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ToString.Include
    private String name;

    @Getter
    @OneToMany(mappedBy = "cargo", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Funcionario> funcionarios;
}
