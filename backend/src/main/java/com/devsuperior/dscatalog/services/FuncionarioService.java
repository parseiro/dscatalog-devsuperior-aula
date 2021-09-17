package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FuncionarioService {
    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    CargoRepository cargoRepository;

    @Transactional(readOnly = true) // tem que ser o import do Hibernate (não do Javax)
    public List<FuncionarioDTO> findAll() {
        return funcionarioRepository.findAll()
                .parallelStream()
                .map(FuncionarioDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FuncionarioDTO findById(Long id) {
        var entity = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional(readOnly = false)
    public FuncionarioDTO insert(FuncionarioDTO dto) {
        final var entity = new FuncionarioEntity();
        copyDtoToEntity(dto, entity);
        final var savedEntity = funcionarioRepository.save(entity);
        return new FuncionarioDTO(savedEntity);
    }

    @Transactional(readOnly = false)
    public FuncionarioDTO update(Long id, final FuncionarioDTO dto) {
        try {

            // cria apenas uma refência, sem puxar do banco de dados
            final var entity = funcionarioRepository.getOne(id);
            copyDtoToEntity(dto, entity);
            final var savedEntity = funcionarioRepository.save(entity);
            return new FuncionarioDTO(savedEntity);
        } catch (javax.persistence.EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    // não se coloca @Transactional aqui pois queremos que venha a exception
    public void delete(Long id) {
        try {
            funcionarioRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            // tentou deletar um ID que nao existe
            throw new ResourceNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            // esta entidade está sendo usada por outra, não pode ser deletada
            throw new DatabaseException("Integrity violation");
        }
    }

    @Transactional(readOnly = true)
    public Page<FuncionarioDTO> findAllPaged(PageRequest pageRequest) {
        return funcionarioRepository.findAll(pageRequest)
                .map(FuncionarioDTO::new);
    }

    private void copyDtoToEntity(@NonNull FuncionarioDTO dto, @NonNull FuncionarioEntity entity) {
        assert(dto.getCargo() != null);

        entity.setNome(dto.getName());
        entity.setSexo(dto.getSexo());
        entity.setTelefone(dto.getTelefone());

        CargoEntity cargo = cargoRepository.getOne(dto.getCargo().getId());
        entity.setCargo(cargo);
    }
}
