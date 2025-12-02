package com.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.model.GoogleLoginStats;

public interface GoogleLoginStatsRepository extends MongoRepository<GoogleLoginStats, String> {
    
}
