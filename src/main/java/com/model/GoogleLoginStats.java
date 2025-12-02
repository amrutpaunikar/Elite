package com.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "google_login_stats")
public class GoogleLoginStats {

    @Id
    private String id;

    private String gemail;
    private String gname;
    private String gpicture;
    private Date gloginTime;

    public GoogleLoginStats(String email, String name, String picture, Date loginTime) {
        this.gemail = email;
        this.gname = name;
        this.gpicture = picture;
        this.gloginTime = loginTime;
    }

   
}
