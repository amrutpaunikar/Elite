package com.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "locations")
@Data @AllArgsConstructor @NoArgsConstructor
public class Locations {

    @Id
    private Long srNo;

    private String role;
    private String location;
    private String region;
    private Boolean popular;


}
