package com.example.myproject.repo;

//Backend: Repository Interface (CardRepository.java)
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.myproject.modal.CardPackage;

@Repository
public interface PackageRepo extends MongoRepository<CardPackage, String> {
}

