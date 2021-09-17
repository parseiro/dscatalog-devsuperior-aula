package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CargoDTO;
import com.devsuperior.dscatalog.dto.FuncionarioDTO;
import com.devsuperior.dscatalog.entities.FuncionarioEntity;
import com.devsuperior.dscatalog.repository.CargoRepository;
import com.devsuperior.dscatalog.repository.FuncionarioRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import lombok.NonNull;
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
public class FuncionarioService implements CrudService<FuncionarioEntity, FuncionarioDTO, Long> {
    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    CargoRepository cargoRepository;

    @Override
    @Transactional(readOnly = true) // tem que ser o import do Hibernate (não do Javax)
    public List<FuncionarioDTO> findAll() {
        return funcionarioRepository.findAll()
                .parallelStream()
                .map(FuncionarioDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FuncionarioDTO findById(Long id) {
        var entity = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return new FuncionarioDTO(entity);
    }

    @Override
    @Transactional(readOnly = false)
    public FuncionarioDTO insert(FuncionarioDTO dto) {
//        System.err.println("DTO: " + dto);
        final var entity = new FuncionarioEntity();
        copyDtoToEntity(dto, entity);
//        System.err.println("Entity: " + entity);
        final var savedEntity = funcionarioRepository.save(entity);
//        System.err.println("Saved entity: " + savedEntity);
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO(savedEntity);
//        System.err.println("FuncionarioDTO: " + funcionarioDTO);
        return funcionarioDTO;
    }

    @Transactional(readOnly = false)
    public FuncionarioDTO insert(FuncionarioDTO funcionarioDto, CargoDTO newCargoDto, CargoService cargoService) {
        final var CargoDTO = cargoService.insert(newCargoDto);
        funcionarioDto.setCargo(newCargoDto);
        return insert(funcionarioDto);
    }

    @Override
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
    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Page<FuncionarioDTO> findAllPaged(PageRequest pageRequest) {
        return funcionarioRepository.findAll(pageRequest)
                .map(FuncionarioDTO::new);
    }

    private void copyDtoToEntity(@NonNull FuncionarioDTO dto, @NonNull FuncionarioEntity entity) {
//        assert(dto.getCargo() != null);

        entity.setName(dto.getName());
        entity.setSexo(dto.getSexo());
        entity.setTelefone(dto.getTelefone());

        if (dto.getCargo() != null) {
            var cargo = cargoRepository.getOne(dto.getCargo().getId());
            entity.setCargo(cargo);
        }
    }


/*    public FuncionarioEntity createNewEntityFromDto(FuncionarioDTO dto) {
        var entity = new FuncionarioEntity();
        copyDtoToEntity(dto, entity);
        return entity;
    }*/
}
