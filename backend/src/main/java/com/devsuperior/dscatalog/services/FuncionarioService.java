package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.entities.Cargo;
import com.devsuperior.dscatalog.entities.Funcionario;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioService {
    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    CargoRepository cargoRepository;

    @Transactional(readOnly = true)
    public void quantosFuncionarios() {
        long quantos = funcionarioRepository.count();
        System.out.println("// Há no repositório " + quantos + " funcionários");
    }

    @Transactional(readOnly = true)
    public void printFuncionariosSortByName() {
        List<Funcionario> funcionarios = funcionarioRepository.findAll(Sort.by("name"));
        System.out.println("Todos os funcionários ordenados por nome crescente");
        funcionarios.forEach(s -> {
            System.out.println("// Funcionario: " + s);
        });
    }

    @Transactional(readOnly = true)
    public void printFuncionariosComCargo() {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        funcionarios.forEach(s -> {
            System.out.println("// Funcionario: " + s);
        });
    }

    @Transactional(readOnly = true) // tem que ser o import do Hibernate (não do Javax)
    public List<Funcionario> findAll() {
        return funcionarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Funcionario findById(Long id) {
        var entity = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return entity;
    }

    @Transactional(readOnly = false)
    public Funcionario insert(Funcionario entity) {
        return funcionarioRepository.save(entity);
    }

    @Transactional(readOnly = false)
    public Funcionario insert(Funcionario funcionarioDto, Cargo cargoDto) {
        Cargo newCargo = new Cargo();
        newCargo.setName(cargoDto.getName());

        newCargo = cargoRepository.save(newCargo);
        funcionarioDto.setCargo(newCargo);
        return funcionarioRepository.save(funcionarioDto);
    }

    @Transactional(readOnly = false)
    public Funcionario update(Long id, final Funcionario dto) {
        try {

            // cria apenas uma refência, sem puxar do banco de dados
            final var entity = funcionarioRepository.getOne(id);
            copyDtoToEntity(dto, entity);
            return funcionarioRepository.save(entity);
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
    public Page<Funcionario> findAllPaged(PageRequest pageRequest) {
        return funcionarioRepository.findAll(pageRequest);
    }

    private void copyDtoToEntity(@NonNull Funcionario dto, @NonNull Funcionario entity) {
//        assert(dto.getCargo() != null);

        entity.setName(dto.getName());
        entity.setSexo(dto.getSexo());
        entity.setTelefone(dto.getTelefone());

        if (dto.getCargo() != null) {
            var cargo = cargoRepository.getOne(dto.getCargo().getId());
            entity.setCargo(cargo);
        }
    }

    public void deleteAll() {
        funcionarioRepository.deleteAll();
    }
}
