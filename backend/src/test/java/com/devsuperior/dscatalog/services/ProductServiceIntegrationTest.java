package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional // para fazer rollback ao final de cada teste
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setup() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        var countBefore = productRepository.count();

        productService.delete(existingId);

        var countAfter = productRepository.count();
        assertThat(countAfter).isEqualTo(countBefore-1);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.delete(nonExistingId)
        );
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPage0AndSize10() {
        final int pageSize = 10;
        final int pageNumber = 0;
        var page = PageRequest.of(pageNumber, pageSize);

        var result = productService.findAllPaged(page);

        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getNumber()).isEqualTo(pageNumber);
        assertThat(result.getSize()).isEqualTo(pageSize);
        assertThat(result.getTotalElements()).isEqualTo(countTotalProducts);
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortedByName() {
        final int pageSize = 10;
        final int pageNumber = 0;
        final Sort sort = Sort.by("name");
        var page = PageRequest.of(pageNumber, pageSize, sort);

        var result = productService.findAllPaged(page);

        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getContent().get(0).getName()).isEqualTo("Macbook Pro");
        assertThat(result.getContent().get(1).getName()).isEqualTo("PC Gamer");
        assertThat(result.getContent().get(2).getName()).isEqualTo("PC Gamer Alfa");
    }
}
