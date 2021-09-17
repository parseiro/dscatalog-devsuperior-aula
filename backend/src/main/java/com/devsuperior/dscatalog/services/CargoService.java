package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CargoDTO;
import com.devsuperior.dscatalog.entities.CargoEntity;
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
import java.util.stream.Collectors;

@Service
public class CargoService {
    @Autowired
    CargoRepository cargoRepository;

    @Transactional(readOnly = true) // tem que ser o import do Hibernate (não do Javax)
    public List<CargoDTO> findAll() {
        return cargoRepository.findAll()
                .parallelStream()
                .map(CargoDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CargoDTO findById(Long id) {
        var entity = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return new CargoDTO(entity);
    }

    @Transactional(readOnly = false)
    public CargoDTO insert(CargoDTO dto) {
        var entity = new CargoEntity();
        entity.setName(dto.getName());
        entity = cargoRepository.save(entity);
        return new CargoDTO(entity);
    }

    @Transactional(readOnly = false)
    public CargoDTO update(Long id, final CargoDTO dto) {
        try {

            // cria apenas uma refência, sem puxar do banco de dados
            var entity = cargoRepository.getOne(id);

            entity.setName(dto.getName());

            var newEntity = cargoRepository.save(entity);

            return new CargoDTO(newEntity);
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
    public Page<CargoDTO> findAllPaged(PageRequest pageRequest) {
        return cargoRepository.findAll(pageRequest)
                .map(CargoDTO::new);
    }
}
