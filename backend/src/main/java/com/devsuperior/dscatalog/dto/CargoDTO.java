package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.entities.CargoEntity;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
public class CargoDTO implements Serializable {
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Getter
    @Setter
    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;

    @Getter
    private Set<FuncionarioDTO> funcionarios = new HashSet<>();

    public CargoDTO(CargoEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
