package com.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.model.Product;

public interface ProductRepository extends MongoRepository<Product, String>  {
	
	List<Product> findByTitleRegexIgnoreCase(String regex);
	
}
