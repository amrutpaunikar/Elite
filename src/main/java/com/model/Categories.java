package com.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Document(collection = "categories")
@Data @AllArgsConstructor @NoArgsConstructor
public class Categories {

    @Id
    private String id;       // MongoDB unique ID

    private Long srNo;
    private String role;
    private String category;
    private String product;
    private Boolean popular;
}