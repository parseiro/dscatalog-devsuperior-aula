package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CargoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CrudService<E, D, I> {
    List<D> findAll();

    D findById(I id);

    D insert(D dto);

    D update(I id, D dto);

    void delete(I id);

    Page<D> findAllPaged(PageRequest pageRequest);
}
