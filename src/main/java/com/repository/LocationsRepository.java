package com.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.model.Locations;

public interface LocationsRepository extends MongoRepository<Locations, Long> {

}