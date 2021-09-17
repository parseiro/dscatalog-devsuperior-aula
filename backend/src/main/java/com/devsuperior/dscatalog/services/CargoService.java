package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.entities.Cargo;
import com.devsuperior.dscatalog.repository.CargoRepository;
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

@Service
public class CargoService {
    @Autowired
    CargoRepository cargoRepository;

    @Transactional(readOnly = true) // tem que ser o import do Hibernate (não do Javax)
    public List<Cargo> findAll() {
        return cargoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cargo findById(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo not found"));
    }

    @Transactional(readOnly = false)
    public Cargo insert(Cargo dto) {
        return cargoRepository.save(dto);
    }

    @Transactional(readOnly = false)
    public Cargo update(Long id, final Cargo dto) {
        try {

            // cria apenas uma refência, sem puxar do banco de dados
            final var entity = cargoRepository.getOne(id);
            entity.setName(dto.getName());
            return cargoRepository.save(entity);
        } catch (javax.persistence.EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    // não se coloca @Transactional aqui pois queremos que venha a exception
    public void delete(Long id) {
        try {
            cargoRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            // tentou deletar um ID que nao existe
            throw new ResourceNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            // esta entidade está sendo usada por outra, não pode ser deletada
            throw new DatabaseException("Integrity violation");
        }
    }

    @Transactional(readOnly = true)
    public Page<Cargo> findAllPaged(PageRequest pageRequest) {
        return cargoRepository.findAll(pageRequest);
    }

}
