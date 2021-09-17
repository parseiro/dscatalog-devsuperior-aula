package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CargoDTO;
import com.devsuperior.dscatalog.dto.FuncionarioDTO;
import com.devsuperior.dscatalog.entities.CargoEntity;
import com.devsuperior.dscatalog.entities.FuncionarioEntity;
import com.devsuperior.dscatalog.repository.CargoRepository;
import com.devsuperior.dscatalog.repository.FuncionarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FuncionarioServiceTest {
    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Autowired
    @InjectMocks
    private FuncionarioService funcionarioService;

    @Autowired
    private CargoService cargoService;

    List<CargoEntity> cargoEntityList;
    List<FuncionarioEntity> funcionarioEntityList;

    @BeforeEach
    public void setUp() {
        cargoEntityList = new ArrayList<>();
        funcionarioEntityList = new ArrayList<>();

        when(funcionarioRepository.save(any())).thenAnswer(i -> {
            final var argument = i.<FuncionarioEntity>getArgument(0);

            final var savedEntity = new FuncionarioEntity();
            savedEntity.setId(funcionarioEntityList.size() + 1L);
            savedEntity.setName(argument.getName());
            savedEntity.setSexo(argument.getSexo());
            savedEntity.setTelefone(argument.getTelefone());
            savedEntity.setCargo(argument.getCargo());

            this.funcionarioEntityList.add(savedEntity);

            return savedEntity;
        });

        when(cargoRepository.getOne(anyLong())).thenAnswer(i -> {
            final var id = i.<Long>getArgument(0);
            return cargoEntityList.parallelStream().filter(s -> s.getId().equals(id)).findAny().get();
        });

        when(cargoRepository.save(any())).thenAnswer(i -> {
            final var argument = i.<CargoEntity>getArgument(0);

            final var savedEntity = new CargoEntity();
            savedEntity.setId(cargoEntityList.size() + 1L);
            savedEntity.setName(argument.getName());

            this.cargoEntityList.add(savedEntity);

            return savedEntity;
        });

        {
            var cargo = new CargoEntity();
            cargo.setId(1L);
            cargo.setName("RH");
            cargoEntityList.add(cargo);

            {
                var funcionario = new FuncionarioEntity();
                funcionario.setId(1L);
                funcionario.setName("Thomas Kaique Paulo Assis");
                funcionario.setSexo("M");
                funcionario.setTelefone("(92) 98305-4037");
                funcionario.setCargo(cargo);
                funcionarioEntityList.add(funcionario);
            }
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
        funcionarioEntityList = null;
    }

    @Test
    void findAll() {
        when(funcionarioRepository.findAll()).thenReturn(funcionarioEntityList);

        final var returnedDtoList = funcionarioService.findAll();
        final var returnedEntityList = returnedDtoList.parallelStream()
                //.map(funcionarioService::createNewEntityFromDto)
                .map(s -> {
                    final var entity = new FuncionarioEntity();
                    entity.setId(s.getId());
                    entity.setName(s.getName());
                    entity.setTelefone(s.getTelefone());
                    entity.setCargo(new CargoEntity(s.getCargo()));
                    return entity;
                })
                .collect(Collectors.toList());

/*        System.err.println("Lista original: " + funcionarioEntityList);
        System.err.println("Lista retornada: " + returnedEntityList);*/
        assertThat(returnedEntityList).containsExactlyInAnyOrderElementsOf(funcionarioEntityList);
    }

    @Test
    void findById() {
        long searchedId = 1L;

        final var entity = funcionarioEntityList.parallelStream().filter(s -> s.getId() == searchedId).findAny();
        final var originalDto = new FuncionarioDTO(entity.get());

        when(funcionarioRepository.findById(searchedId)).thenReturn(entity);

        final var returnerDto = funcionarioService.findById(searchedId);

//        assertThat(originalDto.getId()).isEqualTo(returnerDto.getId());
//        assertThat(originalDto.getName()).isEqualTo(returnerDto.getName());
        assertThat(returnerDto).hasSameHashCodeAs(originalDto);

    }

    @Test
    void insertWithExistingCargo() {
        final long lastId = funcionarioEntityList.size();

        // the item is not there yet
        assertThat(funcionarioEntityList.parallelStream().filter(s -> s.getId() == (lastId + 1)).findAny()).isEmpty();

        String newName = "Online Shopping";
        String sexo = "X";
        String telefone = "666";
        FuncionarioDTO newEntityDTO, savedDto;
        var cargo = cargoEntityList.parallelStream().filter(s -> s.getId().equals(1L)).findAny().get();
        {
            newEntityDTO = new FuncionarioDTO();
            newEntityDTO.setId(3L); // must be ignored
            newEntityDTO.setName(newName);
            newEntityDTO.setSexo(sexo);
            newEntityDTO.setTelefone(telefone);
            newEntityDTO.setCargo(new CargoDTO(cargo));
            savedDto = funcionarioService.insert(newEntityDTO);
        }


//        System.err.println("Entidade enviada: " + newEntityDTO);
//        System.err.println("Entidade recebida: " + savedDto);

        final var savedEntity = funcionarioEntityList.parallelStream().filter(s -> s.getId() == (lastId + 1)).findAny();
        assertThat(savedEntity).isPresent();
        assertThat(savedEntity.get().getId()).isEqualTo(lastId + 1);
        assertThat(savedEntity.get().getName()).isEqualTo(newName);
        assertThat(savedEntity.get().getSexo()).isEqualTo(sexo);
        assertThat(savedEntity.get().getTelefone()).isEqualTo(telefone);
        assertThat(savedEntity.get().getCargo()).isEqualTo(cargo);
    }

    @Test
    void insertWithNewCargo() {
        when(cargoRepository.save(any())).thenAnswer(i -> {
            final var argument = i.<CargoEntity>getArgument(0);

            final var savedEntity = new CargoEntity();
            savedEntity.setId(cargoEntityList.size() + 1L);
            savedEntity.setName(argument.getName());

            this.cargoEntityList.add(savedEntity);

            return savedEntity;
        });

        final long lastId = funcionarioEntityList.size();

        // the item is not there yet
        assertThat(funcionarioEntityList.parallelStream().filter(s -> s.getId() == (lastId + 1)).findAny()).isEmpty();

        String newName = "Online Shopping";
        String sexo = "X";
        String telefone = "666";
        FuncionarioDTO newEntityDTO, savedDto;

        var newCargo = new CargoEntity();
        newCargo.setId(cargoEntityList.size() + 1L);
        newCargo.setName("New Cargo");

        {
            newEntityDTO = new FuncionarioDTO();
            newEntityDTO.setId(3L); // must be ignored
            newEntityDTO.setName(newName);
            newEntityDTO.setSexo(sexo);
            newEntityDTO.setTelefone(telefone);
            CargoDTO newCargoDto = new CargoDTO(newCargo);
            newEntityDTO.setCargo(newCargoDto);
            savedDto = funcionarioService.insert(newEntityDTO, newCargoDto, cargoService);
        }


//        System.err.println("Entidade enviada: " + newEntityDTO);
//        System.err.println("Entidade recebida: " + savedDto);

        final var savedEntity = funcionarioEntityList.parallelStream().filter(s -> s.getId() == (lastId + 1)).findAny();
        assertThat(savedEntity).isPresent();
        assertThat(savedEntity.get().getId()).isEqualTo(lastId + 1);
        assertThat(savedEntity.get().getName()).isEqualTo(newName);
        assertThat(savedEntity.get().getSexo()).isEqualTo(sexo);
        assertThat(savedEntity.get().getTelefone()).isEqualTo(telefone);
        assertThat(savedEntity.get().getCargo()).isEqualTo(newCargo);
    }

/*    @Test
    void update() {
    }*/

    @Test
    void delete() {
        long id = 1L;

        final var entity = funcionarioEntityList.parallelStream().filter(s -> s.getId() == id).findAny().get();

        final var list = funcionarioEntityList;
        doAnswer(invocation -> {
            var id1 = invocation.<Long>getArgument(0);
            list.removeIf(s -> s.getId().equals(id1));
            return null;
        }).when(funcionarioRepository).deleteById(id);

        assertThat(funcionarioEntityList).containsOnlyOnce(entity);

        funcionarioService.delete(id);

        assertThat(funcionarioEntityList).doesNotContain(entity);
    }

/*    @Test
    void findAllPaged() {
    }*/
}