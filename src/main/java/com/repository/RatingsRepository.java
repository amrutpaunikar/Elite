package com.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.model.Ratings;

public interface RatingsRepository extends MongoRepository<Ratings, Long> {
    // You can add custom query methods if needed, e.g. findByShop, findByCategories, etc.
}
