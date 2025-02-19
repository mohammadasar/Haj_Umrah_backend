package com.example.myproject.repo;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.myproject.modal.youtubeVideo;

@Repository
public interface youtubeVideoRepo extends MongoRepository<youtubeVideo, String> {
    // You can add custom query methods here if needed
}
