package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repository.CategoryRepository;
import com.devsuperior.dscatalog.repository.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Transactional(readOnly = true) // tem que ser o import do Hibernate (não do Javax)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Transactional(readOnly = false)
    public Product insert(Product dto) {
        final var entity = copyDtoToEntity(dto, new Product());
        return productRepository.save(entity);
    }

    @Transactional(readOnly = false)
    public Product update(Long id, final Product dto) {
        try {

            // cria apenas uma refência, sem puxar do banco de dados
            final var entity = productRepository.getOne(id);
            copyDtoToEntity(dto, entity);
            final var savedEntity = productRepository.save(entity);
            return savedEntity;
        } catch (javax.persistence.EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    // não se coloca @Transactional aqui pois queremos que venha a exception
    public void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            // tentou deletar um ID que nao existe
            throw new ResourceNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            // esta entidade está sendo usada por outra, não pode ser deletada
            throw new DatabaseException("Integrity violation");
        }
    }

    @Transactional(readOnly = true)
    public Page<Product> findAllPaged(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product copyDtoToEntity(final Product dto, final Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        Set<Category> entityCategories = entity.getCategories();
//        entityCategories.addAll(dto.getCategories());

        dto.getCategories().stream()
                .map(Category::getId)
                .map(categoryRepository::getOne)
                .forEach(entityCategories::add);
        return entity;
    }

    public Product copyDtoToEntity(final com.devsuperior.dscatalog.dto.ProductDTO dto, final Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        Set<Category> entityCategories = entity.getCategories();
//        entityCategories.addAll(dto.getCategories());

        dto.getCategories().stream()
                .map(com.devsuperior.dscatalog.dto.CategoryDTO::getId)
                .map(categoryRepository::getOne)
                .forEach(entityCategories::add);
        return entity;
    }
}
