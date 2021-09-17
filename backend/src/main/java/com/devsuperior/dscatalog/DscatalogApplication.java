package com.devsuperior.dscatalog;

import com.devsuperior.dscatalog.entities.Cargo;
import com.devsuperior.dscatalog.entities.Funcionario;
import com.devsuperior.dscatalog.services.CargoService;
import com.devsuperior.dscatalog.services.FuncionarioService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DscatalogApplication implements CommandLineRunner {
    @Autowired
    CargoService cargoService;

    @Autowired
    FuncionarioService funcionarioService;

    public static void main(String[] args) {
        SpringApplication.run(DscatalogApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        cargoService.deleteAll();
        funcionarioService.deleteAll();

        Cargo c1 = new Cargo();
        c1.setName("Dev");
        c1 = cargoService.insert(c1);

        Cargo c2 = new Cargo();
        c2.setName("Diretor Executivo");
        c2 = cargoService.insert(c2);

        Cargo c3 = new Cargo();
        c3.setName("Designer");
        c3 = cargoService.insert(c3);

        {
            var funcionario = new Funcionario();
//            funcionario.setId(1L);
            funcionario.setName("Thomas Kaique Paulo Assis");
            funcionario.setSexo("M");
            funcionario.setTelefone("(92) 98305-4037");
            funcionario.setCargo(c1);
            funcionarioService.insert(funcionario);
        }
        {
            var funcionario = new Funcionario();
//            funcionario.setId(1L);
            funcionario.setName("Tânia Emilly Isis Ribeiro");
            funcionario.setSexo("F");
            funcionario.setTelefone("(67) 98484-9182");
            funcionario.setCargo(c1);
            funcionarioService.insert(funcionario);
        }
        {
            var funcionario = new Funcionario();
//            funcionario.setId(1L);
            funcionario.setName("Lívia Letícia Castro");
            funcionario.setSexo("F");
            funcionario.setTelefone("(98) 98676-2832");
            funcionario.setCargo(c3);
            funcionarioService.insert(funcionario);
        }

        try {
            cargoService.delete(2L);
        } catch (ResourceNotFoundException e) {
            System.out.println("Tentou deletar um cargo que não existe!");
        }
        try {
            funcionarioService.delete(3L);
        } catch (ResourceNotFoundException e) {
            System.out.println("Tentou deletar um funcionário que não existe!");
        }

        cargoService.printCargosComFuncionarios();
        funcionarioService.printFuncionariosComCargo();
        funcionarioService.printFuncionariosSortByName();
        funcionarioService.quantosFuncionarios();
    }
}
