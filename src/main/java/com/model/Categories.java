package com.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categories {

    @Id
    private String id;

    @Indexed
    private Long srNo;

    @Indexed
    private String role;

    @Indexed
    private String category;

    private String product;

    @Indexed
    private Boolean popular;
}
