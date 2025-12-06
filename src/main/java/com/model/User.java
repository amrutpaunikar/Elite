package com.model;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password; // BCrypt hashed
    private List<String> recentSearches = new ArrayList<>();

    private String otp;
    private LocalDateTime otpExpiry;

    
    public User(String email, String password){
        this.email = email;
        this.password = password;
    }
    
    

}
