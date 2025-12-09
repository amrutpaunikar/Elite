package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.dto.CategoryDTO;
import com.model.Categories;
import com.repository.CategoriesRepository;

@CrossOrigin(origins = "http://localhost:3000") // restrict CORS
@RestController
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoriesRepository repo;

    // CREATE
    @PostMapping
    public Categories createCategory(@RequestBody Categories cat) {
        return repo.save(cat);
    }

    // ✅ FAST PAGINATED READ
    @GetMapping("/allCategories")
    public List<CategoryDTO> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        size = Math.min(size, 20); // prevent abuse

        Pageable pageable = PageRequest.of(page, size);

        return repo.findBy(pageable)
                   .stream()
                   .map(c -> new CategoryDTO(
                           c.getId(),
                           c.getCategory(),
                           c.getPopular()))
                   .toList();
    }

    // READ ONE
    @GetMapping("/{id}")
    public Categories getCategory(@PathVariable String id) {
        return repo.findById(id).orElse(null);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Categories updateCategory(
            @PathVariable String id,
            @RequestBody Categories newData) {

        return repo.findById(id).map(cat -> {
            cat.setSrNo(newData.getSrNo());
            cat.setRole(newData.getRole());
            cat.setCategory(newData.getCategory());
            cat.setProduct(newData.getProduct());
            cat.setPopular(newData.getPopular());
            return repo.save(cat);
        }).orElse(null);
    }

    // DELETE ONE
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable String id) {
        repo.deleteById(id);
    }

    // ✅ FAST DELETE ALL
    @DeleteMapping("/delete-all")
    public void deleteAllCategories() {
        repo.deleteAll(); // ok for small data
    }

    // ✅ BATCH INSERT
    @PostMapping("/bulk")
    public List<Categories> bulkInsert(@RequestBody List<Categories> list) {
        return repo.saveAll(list);
    }
}
