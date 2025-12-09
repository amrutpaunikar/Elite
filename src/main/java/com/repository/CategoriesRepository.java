package com.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.model.Categories;

@Repository
public interface CategoriesRepository extends MongoRepository<Categories, String> {

}

