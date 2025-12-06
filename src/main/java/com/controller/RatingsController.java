package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.model.Ratings;
import com.repository.RatingsRepository;

@CrossOrigin("*")
@RestController
@RequestMapping("/ratings")
public class RatingsController {

    @Autowired
    private RatingsRepository repo;

    // CREATE
    @PostMapping("/add")
    public ResponseEntity<Ratings> add(@RequestBody Ratings r) {
        return ResponseEntity.ok(repo.save(r));
    }

    // CREATE BULK
    @PostMapping("/bulk")
    public ResponseEntity<List<Ratings>> addBulk(@RequestBody List<Ratings> list) {
        return ResponseEntity.ok(repo.saveAll(list));
    }

    @GetMapping("/allRatings")
public ResponseEntity<List<Ratings>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    // MongoRepository supports this
    org.springframework.data.domain.Page<Ratings> pageData = repo.findAll(pageable);

    return ResponseEntity.ok(pageData.getContent());
}
    

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Ratings> getById(@PathVariable Long id) {
        return ResponseEntity.ok(repo.findById(id).orElse(null));
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<Ratings> update(@PathVariable Long id, @RequestBody Ratings newData) {
        Ratings old = repo.findById(id).orElse(null);
        if (old == null) return ResponseEntity.notFound().build();

        old.setCategories(newData.getCategories());
        old.setShop(newData.getShop());
        old.setRating(newData.getRating());
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
