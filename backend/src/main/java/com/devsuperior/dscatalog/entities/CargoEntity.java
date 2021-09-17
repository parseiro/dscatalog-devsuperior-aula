package com.devsuperior.dscatalog.entities;

import com.devsuperior.dscatalog.dto.CargoDTO;
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
public class CargoEntity implements Serializable {
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ToString.Include
    private String name;

    @Getter
    @OneToMany(mappedBy = "cargo")
    private Set<FuncionarioEntity> funcionarios;

    public CargoEntity(CargoDTO dto) {
        super();
        this.name = dto.getName();
    }
}
