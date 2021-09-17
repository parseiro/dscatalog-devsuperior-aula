package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.FuncionarioDTO;
import com.devsuperior.dscatalog.entities.CargoEntity;
import com.devsuperior.dscatalog.entities.FuncionarioEntity;
import com.devsuperior.dscatalog.repository.CargoRepository;
import com.devsuperior.dscatalog.repository.FuncionarioRepository;
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

        return new FuncionarioDTO(entity);
    }

    @Transactional(readOnly = false)
    public FuncionarioDTO insert(FuncionarioDTO dto) {
        var entity = new FuncionarioEntity();
        copyDtoToEntity(dto, entity);
        entity = funcionarioRepository.save(entity);
        return new FuncionarioDTO(entity);
    }

    @Transactional(readOnly = false)
    public FuncionarioDTO update(Long id, final FuncionarioDTO dto) {
        try {

            // cria apenas uma refência, sem puxar do banco de dados
            var entity = funcionarioRepository.getOne(id);
            copyDtoToEntity(dto, entity);

            var newEntity = funcionarioRepository.save(entity);

            return new FuncionarioDTO(newEntity);
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

    private void copyDtoToEntity(FuncionarioDTO dto, FuncionarioEntity entity) {
        entity.setNome(dto.getName());
        entity.setSexo(dto.getSexo());
        entity.setTelefone(dto.getTelefone());

        CargoEntity cargo = cargoRepository.getOne(dto.getCargo().getId());
        entity.setCargo(cargo);
    }
}
