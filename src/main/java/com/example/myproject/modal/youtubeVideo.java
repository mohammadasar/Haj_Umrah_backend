package com.example.myproject.modal;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "videos") // Specifies the MongoDB collection name
public class youtubeVideo{

    @Id
    private ObjectId id; // MongoDB uses String type for IDs

    private String name;
    private String url;

    // Getters and Setters
    public ObjectId  getId() {
        return id;
    }

    public void setId(ObjectId  id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

