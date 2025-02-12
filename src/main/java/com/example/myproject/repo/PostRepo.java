package com.example.myproject.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.myproject.modal.Post;

public interface PostRepo extends MongoRepository<Post, String> {

}
