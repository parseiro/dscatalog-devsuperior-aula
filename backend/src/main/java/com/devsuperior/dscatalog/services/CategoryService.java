package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repository.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Transactional(readOnly = true) // tem que ser o import do Hibernate (não do Javax)
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .parallelStream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        final var entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return new CategoryDTO(entity);
    }

    @Transactional(readOnly = false)
    public CategoryDTO insert(CategoryDTO dto) {
        final var entity = new Category();
        entity.setName(dto.getName());
        Category savedEntity = categoryRepository.save(entity);
        return new CategoryDTO(savedEntity);
    }

    @Transactional(readOnly = false)
    public CategoryDTO update(Long id, final CategoryDTO dto) {
        try {

            // cria apenas uma refência, sem puxar do banco de dados
            final var entity = categoryRepository.getOne(id);

            entity.setName(dto.getName());

            Category savedEntity = categoryRepository.save(entity);

            return new CategoryDTO(savedEntity);
        } catch (javax.persistence.EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    // não se coloca @Transactional aqui pois queremos que venha a exception
    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            // tentou deletar um ID que nao existe
            throw new ResourceNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            // esta entidade está sendo usada por outra, não pode ser deletada
            throw new DatabaseException("Integrity violation");
        }
    }

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
        return categoryRepository.findAll(pageRequest)
                .map(CategoryDTO::new);
    }
}
