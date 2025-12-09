package com.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.model.Categories;

@Repository
public interface CategoriesRepository extends MongoRepository<Categories, String> {

    List<Categories> findBy(Pageable pageable);

}
