package com.devsuperior.dscatalog.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CrudService<E, D, I> {
    List<D> findAll();

    D findById(I id);

    D insert(D dto);

    D update(I id, D dto);

    void delete(I id);

    Page<D> findAllPaged(PageRequest pageRequest);
}
