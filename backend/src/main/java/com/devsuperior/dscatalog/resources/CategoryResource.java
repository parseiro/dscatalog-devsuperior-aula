package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.CargoDTO;
import com.devsuperior.dscatalog.services.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/categories")
public class CategoryResource {
    @Autowired
    private CargoService cargoService;


    @GetMapping
    public ResponseEntity<Page<CargoDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy

    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

        Page<CargoDTO> categories = cargoService.findAllPaged(pageRequest);

        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(cargoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CargoDTO> insert(@RequestBody final CargoDTO dto) {
        var newDTO = cargoService.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(newDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargoDTO> update(
            @PathVariable Long id,
            @RequestBody final CargoDTO dto
    ) {
        var newDto = cargoService.update(id, dto);
        return ResponseEntity.ok().body(newDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CargoDTO> delete(@PathVariable Long id) {
        cargoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
