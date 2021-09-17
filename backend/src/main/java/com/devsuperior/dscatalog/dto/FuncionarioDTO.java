package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.entities.FuncionarioEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FuncionarioDTO {
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String sexo;

    @Getter
    @Setter
    private String telefone;

    @Getter
    private CargoDTO cargo;

    public FuncionarioDTO(FuncionarioEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.sexo = entity.getSexo();
        this.telefone = entity.getTelefone();

        if (entity.getCargo() != null) {
            this.cargo = new CargoDTO(entity.getCargo());
        }
    }
}
