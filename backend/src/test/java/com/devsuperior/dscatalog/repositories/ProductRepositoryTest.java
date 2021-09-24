package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static long existingId;
    private static long nonExistingId;

    @BeforeAll
    static void setupAll() {
        existingId = 1L;
        nonExistingId = 1000L;
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        var result = productRepository.findById(existingId);
        Assertions.assertThat(result).isPresent();

        productRepository.deleteById(existingId);

        result = productRepository.findById(existingId);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void deleteShouldThrowWhenIdDoesNotExist() {

        org.junit.jupiter.api.Assertions.assertThrows(
                EmptyResultDataAccessException.class,
                () -> productRepository.deleteById(nonExistingId)
        );
    }

    @Test
    public void saveShouldPersistWithAutoincrement() {
        var lastId = productRepository.findAll().size();
        var newProduct = Product.builder()
                .id(null)
                .name("Novo Produto")
                .description("Descrição")
                .price(BigDecimal.valueOf(3.50))
//                .category(categoryRepository.getOne(1L))
                .build();
/*        var newCategory = Category.builder()
                .id(null)
                .name("Nova categoria")
                .build();*/

        var savedProduct = productRepository.save(newProduct);
//        var savedCategory = categoryRepository.save(newCategory);

        Assertions.assertThat(savedProduct.getId()).isNotNull();
        Assertions.assertThat(savedProduct.getId()).isEqualTo(lastId+1);

    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        var optional = productRepository.findById(existingId);

        Assertions.assertThat(optional).isPresent();
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoNotExist() {
        var optional = productRepository.findById(nonExistingId);

        Assertions.assertThat(optional).isEmpty();
    }
}