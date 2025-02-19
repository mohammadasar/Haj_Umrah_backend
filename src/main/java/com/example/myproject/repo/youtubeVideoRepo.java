package com.example.myproject.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;
import com.example.myproject.modal.youtubeVideo;

@Repository
public interface youtubeVideoRepo extends MongoRepository<youtubeVideo, ObjectId> {
}
