package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.model.Locations;
import com.repository.LocationsRepository;


@CrossOrigin("*")
@RestController
@RequestMapping("/locations")
public class LocationsController {

    @Autowired
    private LocationsRepository repo;

    // CREATE
    @PostMapping("/add")
    public ResponseEntity<Locations> add(@RequestBody Locations location) {
        return ResponseEntity.ok(repo.save(location));
    }

    // CREATE BULK
    @PostMapping("/bulk")
    public ResponseEntity<List<Locations>> addBulk(@RequestBody List<Locations> list) {
        return ResponseEntity.ok(repo.saveAll(list));
    }

    // GET ALL (with pagination)
    @GetMapping("/all")
    public ResponseEntity<List<Locations>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(repo.findAll(pageable).getContent());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Locations> getById(@PathVariable Long id) {
        return ResponseEntity.ok(repo.findById(id).orElse(null));
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<Locations> update(@PathVariable Long id, @RequestBody Locations newData) {
        Locations old = repo.findById(id).orElse(null);
        if (old == null) return ResponseEntity.notFound().build();

        old.setRole(newData.getRole());
        old.setLocation(newData.getLocation());
        old.setRegion(newData.getRegion());
        old.setPopular(newData.getPopular());

        return ResponseEntity.ok(repo.save(old));
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.ok("Deleted ID: " + id);
    }
}
