package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.entities.Cargo;
import com.devsuperior.dscatalog.entities.Funcionario;
import com.devsuperior.dscatalog.repository.CargoRepository;
import com.devsuperior.dscatalog.repository.FuncionarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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

    List<Cargo> cargoList;
    List<Funcionario> funcionarioList;

    @BeforeEach
    public void setUp() {
        cargoList = new ArrayList<>();
        funcionarioList = new ArrayList<>();

        when(funcionarioRepository.save(any())).thenAnswer(i -> {
            final var argument = i.<Funcionario>getArgument(0);

            final var savedEntity = new Funcionario();
            savedEntity.setId(funcionarioList.size() + 1L);
            savedEntity.setName(argument.getName());
            savedEntity.setSexo(argument.getSexo());
            savedEntity.setTelefone(argument.getTelefone());
            savedEntity.setCargo(argument.getCargo());

            this.funcionarioList.add(savedEntity);

            return savedEntity;
        });

        when(cargoRepository.getOne(anyLong())).thenAnswer(i -> {
            final var id = i.<Long>getArgument(0);
            return cargoList.parallelStream().filter(s -> s.getId().equals(id)).findAny().get();
        });

        when(cargoRepository.save(any())).thenAnswer(i -> {
            final Cargo argument = i.<Cargo>getArgument(0);

            final var savedEntity = new Cargo();
            savedEntity.setId(cargoList.size() + 1L);
            savedEntity.setName(argument.getName());

            this.cargoList.add(savedEntity);

            return savedEntity;
        });

        {
            var cargo = new Cargo();
            cargo.setId(1L);
            cargo.setName("RH");
            cargoList.add(cargo);

            {
                var funcionario = new Funcionario();
                funcionario.setId(1L);
                funcionario.setName("Thomas Kaique Paulo Assis");
                funcionario.setSexo("M");
                funcionario.setTelefone("(92) 98305-4037");
                funcionario.setCargo(cargo);
                funcionarioList.add(funcionario);
            }
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
        funcionarioList = null;
    }

    @Test
    void findAll() {
        when(funcionarioRepository.findAll()).thenReturn(funcionarioList);

        final var returnedDtoList = funcionarioService.findAll();
        final var returnedEntityList = returnedDtoList.parallelStream()
                //.map(funcionarioService::createNewEntityFromDto)
                .map(s -> {
                    final var entity = new Funcionario();
                    entity.setId(s.getId());
                    entity.setName(s.getName());
                    entity.setTelefone(s.getTelefone());
                    entity.setCargo(s.getCargo());
                    return entity;
                })
                .collect(Collectors.toList());

/*        System.err.println("Lista original: " + funcionarioEntityList);
        System.err.println("Lista retornada: " + returnedEntityList);*/
        assertThat(returnedEntityList).containsExactlyInAnyOrderElementsOf(funcionarioList);
    }

    @Test
    void findById() {
        long searchedId = 1L;

        final var entity = funcionarioList.parallelStream().filter(s -> s.getId() == searchedId).findAny();

        when(funcionarioRepository.findById(searchedId)).thenReturn(entity);

        final var returnedEntity = funcionarioService.findById(searchedId);

//        assertThat(originalDto.getId()).isEqualTo(returnedEntity.getId());
//        assertThat(originalDto.getName()).isEqualTo(returnedEntity.getName());
        assertThat(returnedEntity).hasSameHashCodeAs(entity);

    }

    @Test
    void insertWithExistingCargo() {
        final long lastId = funcionarioList.size();

        // the item is not there yet
        assertThat(funcionarioList.parallelStream().filter(s -> s.getId() == (lastId + 1)).findAny()).isEmpty();

        String newName = "Online Shopping";
        String sexo = "X";
        String telefone = "666";
        Funcionario newEntityDTO, savedDto;
        var cargo = cargoList.parallelStream().filter(s -> s.getId().equals(1L)).findAny().get();
        {
            newEntityDTO = new Funcionario();
            newEntityDTO.setId(3L); // must be ignored
            newEntityDTO.setName(newName);
            newEntityDTO.setSexo(sexo);
            newEntityDTO.setTelefone(telefone);
            newEntityDTO.setCargo(cargo);
            savedDto = funcionarioService.insert(newEntityDTO);
        }


//        System.err.println("Entidade enviada: " + newEntityDTO);
//        System.err.println("Entidade recebida: " + savedDto);

        final var savedEntity = funcionarioList.parallelStream().filter(s -> s.getId() == (lastId + 1)).findAny();
        assertThat(savedEntity).isPresent();
        assertThat(savedEntity.get().getId()).isEqualTo(lastId + 1);
        assertThat(savedEntity.get().getName()).isEqualTo(newName);
        assertThat(savedEntity.get().getSexo()).isEqualTo(sexo);
        assertThat(savedEntity.get().getTelefone()).isEqualTo(telefone);
        assertThat(savedEntity.get().getCargo()).isEqualTo(cargo);
    }

    @Test
    void insertWithNewCargo() {
        final long lastFuncionarioId = funcionarioList.size();
        final int lastCargoId = cargoList.size();

        // the item is not there yet
        assertThat(funcionarioList.parallelStream().filter(s -> s.getId() == (lastFuncionarioId + 1)).findAny()).isEmpty();

        String newName = "Online Shopping";
        String sexo = "X";
        String telefone = "666";
        Funcionario newEntityDTO, savedDto;

        var newCargoDto = new Cargo();
        newCargoDto.setId(25L); // must be ignored
        String newCargoName = "New Cargo";
        newCargoDto.setName(newCargoName);

        // nao existe este cargo ainda
        assertThat(cargoList.parallelStream().filter(s -> s.getName().equals(newCargoName))).isEmpty();

        {
            newEntityDTO = new Funcionario();
            newEntityDTO.setId(3L); // must be ignored
            newEntityDTO.setName(newName);
            newEntityDTO.setSexo(sexo);
            newEntityDTO.setTelefone(telefone);
            newEntityDTO.setCargo(newCargoDto);
            savedDto = funcionarioService.insert(newEntityDTO, newCargoDto);
        }


//        System.err.println("Entidade enviada: " + newEntityDTO);
//        System.err.println("Entidade recebida: " + savedDto);

        final var savedEntity = funcionarioList.parallelStream().filter(s -> s.getId() == (lastFuncionarioId + 1)).findAny();
        assertThat(savedEntity).isPresent();
        assertThat(savedEntity.get().getId()).isEqualTo(lastFuncionarioId + 1L);
        assertThat(savedEntity.get().getName()).isEqualTo(newName);
        assertThat(savedEntity.get().getSexo()).isEqualTo(sexo);
        assertThat(savedEntity.get().getTelefone()).isEqualTo(telefone);

        Cargo savedCargo = savedEntity.get().getCargo();
        assertThat(savedCargo.getId()).isEqualTo(lastCargoId+1L);
//        System.err.println("Cargo DTO: " + newCargoDto);
//        System.err.println("Cargo entity: " + savedCargo);
        // ja existe
        assertThat(cargoList.parallelStream().filter(s -> s.getName().equals(newCargoName))).isNotEmpty();
    }

/*    @Test
    void update() {
    }*/

    @Test
    void delete() {
        long id = 1L;

        final var entity = funcionarioList.parallelStream().filter(s -> s.getId() == id).findAny().get();

        final var list = funcionarioList;
        doAnswer(invocation -> {
            var id1 = invocation.<Long>getArgument(0);
            list.removeIf(s -> s.getId().equals(id1));
            return null;
        }).when(funcionarioRepository).deleteById(id);

        assertThat(funcionarioList).containsOnlyOnce(entity);

        funcionarioService.delete(id);

        assertThat(funcionarioList).doesNotContain(entity);
    }

/*    @Test
    void findAllPaged() {
    }*/
}