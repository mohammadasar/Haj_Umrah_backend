package com.example.myproject.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Post_Image")
public class Post {
    
    @Id
    private String id;
    private String imageUrl;

    // ✅ Default constructor (needed for MongoDB)
    public Post() {}

    // ✅ Parameterized constructor
    public Post(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
