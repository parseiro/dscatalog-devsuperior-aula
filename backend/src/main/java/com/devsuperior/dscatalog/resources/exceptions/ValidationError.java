package com.devsuperior.dscatalog.resources.exceptions;

import lombok.Getter;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError{
    @Getter @Singular
    private List<FieldMessage> errors = new ArrayList<>();

    public void addError(String field, String message) {
        var error = new FieldMessage(field, message);
        this.errors.add(error);
    }
}
