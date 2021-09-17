package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.entities.Cargo;
import com.devsuperior.dscatalog.repository.CargoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargoServiceTest {
    @Mock
    private CargoRepository cargoRepository;

    @Autowired
    @InjectMocks
    private CargoService cargoService;

    List<Cargo> cargoList;

    @BeforeEach
    public void setUp() {
        cargoList = new ArrayList<>();

        {
            var cargo = new Cargo();
            cargo.setId(1L);
            cargo.setName("RH");
            cargoList.add(cargo);
        }
        {
            var cargo = new Cargo();
            cargo.setId(2L);
            cargo.setName("TI");
            cargoList.add(cargo);
        }
        {
            var cargo = new Cargo();
            cargo.setId(3L);
            cargo.setName("Admin");
            cargoList.add(cargo);
        }
    }

    @AfterEach
    public void tearDown() {
        cargoList = null;
    }

    @Test
    void findAll() {
        when(cargoRepository.findAll()).thenReturn(cargoList);

        final var returnedDtoList = cargoService.findAll();
        final var returnedEntityList = returnedDtoList;

//        System.err.println("Lista original: " + cargoEntityList);
//        System.err.println("Lista retornada: " + returnedEntityList);
        assertThat(returnedEntityList).containsExactlyInAnyOrderElementsOf(cargoList);
    }

    @Test
    void findById() {
        long id = 1L;

        final var cargoEntity = cargoList.parallelStream()
                .filter(s -> s.getId() == id).findAny();
        final var originalDto = cargoEntity.get();

        when(cargoRepository.findById(id)).thenReturn(cargoEntity);

        final var returnerDto = cargoService.findById(id);

//        assertThat(originalDto.getId()).isEqualTo(returnerDto.getId());
//        assertThat(originalDto.getName()).isEqualTo(returnerDto.getName());
        assertThat(returnerDto).hasSameHashCodeAs(originalDto);

    }

    @Test
    void insert() {
        final long lastId = cargoList.size();

        when(cargoRepository.save(any())).thenAnswer(i -> {
            final var argument = i.<Cargo>getArgument(0);

            final var savedEntity = new Cargo();
            savedEntity.setId(lastId + 1);
            savedEntity.setName(argument.getName());

            this.cargoList.add(savedEntity);

            return savedEntity;
        });

        // the item is not there yet
        assertThat(cargoList.parallelStream().filter(s -> s.getId() == (lastId + 1)).findAny()).isEmpty();

        String newName = "Online Shopping";
        Cargo savedDto;
        {
            final var newEntityDTO = new Cargo();
            newEntityDTO.setId(3L); // must be ignored
            newEntityDTO.setName(newName);
            savedDto = cargoService.insert(newEntityDTO);
        }

//        assertThat(cargoDTOList).containsOnlyOnce(savedDto);
        final var entity = cargoList.parallelStream()
                .filter(s -> s.getId() == (lastId + 1)).findAny();
        assertThat(entity).isPresent();
        assertThat(entity.get().getId()).isEqualTo(lastId + 1);
        assertThat(entity.get().getName()).isEqualTo(newName);
    }

/*    @Test
    void update() {
    }*/

    @Test
    void delete() {
        long localId = 1L;

        final var cargoEntity = cargoList.parallelStream()
                .filter(s -> s.getId() == localId).findAny().get();

        final var list = cargoList;

        doAnswer(invocation -> {
                var id = invocation.<Long>getArgument(0);
                list.removeIf(s -> s.getId().equals(id));
                return null;
        }).when(cargoRepository).deleteById(localId);

        assertThat(cargoList).containsOnlyOnce(cargoEntity);

        cargoService.delete(localId);

        assertThat(cargoList).doesNotContain(cargoEntity);
    }

/*    @Test
    void findAllPaged() {
    }*/
}