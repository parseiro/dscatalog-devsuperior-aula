package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CargoDTO;
import com.devsuperior.dscatalog.entities.CargoEntity;
import com.devsuperior.dscatalog.repository.CargoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargoServiceTest {
    @Mock
    private CargoRepository cargoRepository;

    @Autowired
    @InjectMocks
    private CargoService cargoService;

    List<CargoEntity> cargoEntityList;

    @BeforeEach
    public void setUp() {
        cargoEntityList = new ArrayList<>();

        {
            var cargo = new CargoEntity();
            cargo.setId(1L);
            cargo.setName("RH");
            cargoEntityList.add(cargo);
        }
        {
            var cargo = new CargoEntity();
            cargo.setId(2L);
            cargo.setName("TI");
            cargoEntityList.add(cargo);
        }
        {
            var cargo = new CargoEntity();
            cargo.setId(3L);
            cargo.setName("Admin");
            cargoEntityList.add(cargo);
        }
    }

    @AfterEach
    public void tearDown() {
        cargoEntityList = null;
    }

    @Test
    void findAll() {
        when(cargoRepository.findAll()).thenReturn(cargoEntityList);

        final var returnedDtoList = cargoService.findAll();
        final var returnedEntityList = returnedDtoList.parallelStream()
                .map(CargoEntity::new)
                .collect(Collectors.toList());

//        System.err.println("Lista original: " + cargoEntityList);
//        System.err.println("Lista retornada: " + returnedEntityList);
        assertThat(returnedEntityList).containsExactlyInAnyOrderElementsOf(cargoEntityList);
    }

    @Test
    void findById() {
        long id = 1L;

        final var cargoEntity = cargoEntityList.parallelStream()
                .filter(s -> s.getId() == id).findAny();
        final var originalDto = new CargoDTO(cargoEntity.get());

        when(cargoRepository.findById(id)).thenReturn(cargoEntity);

        final var returnerDto = cargoService.findById(id);

//        assertThat(originalDto.getId()).isEqualTo(returnerDto.getId());
//        assertThat(originalDto.getName()).isEqualTo(returnerDto.getName());
        assertThat(returnerDto).hasSameHashCodeAs(originalDto);

    }

    @Test
    void insert() {
        final long lastId = cargoEntityList.size();

        when(cargoRepository.save(any())).thenAnswer(i -> {
            final var argument = i.<CargoEntity>getArgument(0);

            final var savedEntity = new CargoEntity();
            savedEntity.setId(lastId + 1);
            savedEntity.setName(argument.getName());

            this.cargoEntityList.add(savedEntity);

            return savedEntity;
        });

        // the item is not there yet
        assertThat(cargoEntityList.parallelStream().filter(s -> s.getId() == (lastId + 1)).findAny()).isEmpty();

        String newName = "Online Shopping";
        CargoDTO savedDto;
        {
            final var newEntityDTO = new CargoDTO();
            newEntityDTO.setId(3L); // must be ignored
            newEntityDTO.setName(newName);
            savedDto = cargoService.insert(newEntityDTO);
        }

//        assertThat(cargoDTOList).containsOnlyOnce(savedDto);
        final var entity = cargoEntityList.parallelStream()
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

        final var cargoEntity = cargoEntityList.parallelStream()
                .filter(s -> s.getId() == localId).findAny().get();

        final var list = cargoEntityList;

        doAnswer(invocation -> {
                var id = invocation.<Long>getArgument(0);
                list.removeIf(s -> s.getId().equals(id));
                return null;
        }).when(cargoRepository).deleteById(localId);

        assertThat(cargoEntityList).containsOnlyOnce(cargoEntity);

        cargoService.delete(localId);

        assertThat(cargoEntityList).doesNotContain(cargoEntity);
    }

/*    @Test
    void findAllPaged() {
    }*/
}