package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.FuncionarioDTO;
import com.devsuperior.dscatalog.services.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/products")
public class FuncionarioResource {
    @Autowired
    private FuncionarioService funcionarioService;


    @GetMapping
    public ResponseEntity<Page<FuncionarioDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy

    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

        Page<FuncionarioDTO> funcionarios = funcionarioService.findAllPaged(pageRequest);

        return ResponseEntity.ok().body(funcionarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(funcionarioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<FuncionarioDTO> insert(@RequestBody final FuncionarioDTO dto) {
        var newDTO = funcionarioService.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(newDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> update(
            @PathVariable Long id,
            @RequestBody final FuncionarioDTO dto
    ) {
        var newDto = funcionarioService.update(id, dto);
        return ResponseEntity.ok().body(newDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> delete(@PathVariable Long id) {
        funcionarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
