package com.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "ratings")
@Data @AllArgsConstructor @NoArgsConstructor
public class Ratings {

    @Id
    private Long srNo;

    private String categories;
    private String shop;
    private int rating;
    private Boolean popular;
}
