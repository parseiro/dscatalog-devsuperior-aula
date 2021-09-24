package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        product = Product.builder()
                .id(existingId)
                .name("Novo Produto")
                .description("Descrição")
                .price(BigDecimal.valueOf(3.50))
                .build();
        page = new PageImpl<>(List.of(product));

        category = Category.builder()
                .id(1L)
                .name("Nova categoria")
                .build();

        when(productRepository.findAll((Pageable) any())).thenReturn(page);

        when(productRepository.save(any())).thenReturn(product);

        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(productRepository.getOne(existingId)).thenReturn(product);
        when(productRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
        when(categoryRepository.getOne(existingId)).thenReturn(category);

        doNothing().when(productRepository).deleteById(existingId);
        doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
        doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);


    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> result = productService.findAllPaged(pageable);

        org.assertj.core.api.Assertions.assertThat(result).isNotNull();
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(
                () -> productService.delete(existingId)
        );
        verify(productRepository, times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowWhenIdDoesNotExist() {
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> productService.delete(nonExistingId)
        );
        verify(productRepository, times(1)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenItDependendsOnCategory() {
        Assertions.assertThrows(
                DatabaseException.class,
                () -> productService.delete(dependentId)
        );
        verify(productRepository, times(1)).deleteById(dependentId);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {

        var productDto = productService.findById(existingId);

        org.assertj.core.api.Assertions.assertThat(productDto).isNotNull();
        org.assertj.core.api.Assertions.assertThat(productDto.getId()).isEqualTo(existingId);

        verify(productRepository, times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findById(nonExistingId)
        );
    }

    @Test
    public void updateShouldReturnProductDtoWhenIdExists() {
        var productDto = productService.update(product.getId(), product);

        org.assertj.core.api.Assertions.assertThat(productDto).isNotNull();
        org.assertj.core.api.Assertions.assertThat(productDto.getId()).isEqualTo(existingId);

        verify(productRepository, times(1)).getOne(existingId);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> productService.update(nonExistingId, product)
        );
    }
}