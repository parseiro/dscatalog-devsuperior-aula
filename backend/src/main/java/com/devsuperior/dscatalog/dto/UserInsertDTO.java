package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.services.validation.UserInsertValid;
import lombok.Getter;
import lombok.Setter;

@UserInsertValid
public class UserInsertDTO extends UserDTO {
    @Getter @Setter
    private String password;

    public UserInsertDTO() {
        super();
    }
}
