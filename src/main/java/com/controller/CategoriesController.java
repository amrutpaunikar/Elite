package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.model.Categories;
import com.repository.CategoriesRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoriesRepository repo;

    // CREATE
    @PostMapping("/add")
    public ResponseEntity<?> createCategory(@RequestBody Categories cat) {
        return ResponseEntity.ok(repo.save(cat));
    }

    // FIX 1: Add path -> "/list"
    // FIX 2: Default page = 0 (pagination starts at 0)
    @GetMapping("/list")
    public List<Categories> getAllCategories(
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10);
        return repo.findAll(pageable).getContent();
    }

    // READ ONE
    @GetMapping("/{id}")
    public Categories getCategory(@PathVariable String id) {
        return repo.findById(id).orElse(null);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Categories updateCategory(@PathVariable String id, @RequestBody Categories newData) {

        return repo.findById(id).map(cat -> {
            cat.setSrNo(newData.getSrNo());
            cat.setRole(newData.getRole());
            cat.setCategory(newData.getCategory());
            cat.setProduct(newData.getProduct());
            cat.setPopular(newData.getPopular());
            return repo.save(cat);
        }).orElse(null);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable String id) {
        repo.deleteById(id);
        return "Category deleted: " + id;
    }

    // DELETE ALL
    @DeleteMapping("/delete-all")
    public String deleteAllCategories() {
        repo.deleteAll();
        return "All categories deleted successfully!";
    }

    // FIX 3: This remains POST only, so GET will not collide anymore
    @PostMapping("/bulk")
    public List<Categories> bulkInsert(@RequestBody List<Categories> list) {
        return repo.saveAll(list);
    }
}
