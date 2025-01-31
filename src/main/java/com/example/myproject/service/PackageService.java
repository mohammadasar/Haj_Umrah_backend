package com.example.myproject.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.myproject.modal.CardPackage;
import com.example.myproject.repo.PackageRepo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class PackageService {

    private final PackageRepo packageRepo;

    public PackageService(PackageRepo packageRepo) {
        this.packageRepo = packageRepo;
    }

    // Add a new card
    public CardPackage addCard(MultipartFile image, String title1, String title2, String title3, String title4, String title5) {
        String uploadDir = "D:\\\\my_own_projects\\\\backend_projects\\\\myproject\\\\CardImage\\\\";
        String fileName = image.getOriginalFilename();
        Path targetLocation = Paths.get(uploadDir + fileName);

        try {
            // Save the image to the specified directory
            image.transferTo(new java.io.File(uploadDir + fileName));
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file: " + fileName, e);
        }

        // Create and save a new CardPackage object
        CardPackage cardPackage = new CardPackage(fileName, title1, title2, title3, title4, title5);
        return packageRepo.save(cardPackage);
    }

    // Retrieve all cards
    public List<CardPackage> getAllCards() {
        return packageRepo.findAll();
    }

    // Retrieve a card by its ID
    public Optional<CardPackage> getCardById(String id) {
        return packageRepo.findById(id);
    }

    // Update an existing card
    public CardPackage updateCard(String id, MultipartFile image, String title1, String title2, String title3, String title4, String title5) {
        // Fetch the existing CardPackage
        CardPackage cardPackage = packageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + id));

        // If an image is provided, save it and update the file name
        if (image != null && !image.isEmpty()) {
            String uploadDir = "D:\\\\my_own_projects\\\\backend_projects\\\\myproject\\\\CardImage\\\\";
            String fileName = image.getOriginalFilename();

            try {
                image.transferTo(new java.io.File(uploadDir + fileName));
                cardPackage.setImage(fileName);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update file: " + fileName, e);
            }
        }

        // Update other fields
        cardPackage.setTitle1(title1);
        cardPackage.setTitle2(title2);
        cardPackage.setTitle3(title3);
        cardPackage.setTitle4(title4);
        cardPackage.setTitle5(title5);

        return packageRepo.save(cardPackage);
    }

    // Delete a card by its ID
    public void deleteCard(String id) {
        if (!packageRepo.existsById(id)) {
            throw new RuntimeException("Card not found with ID: " + id);
        }
        packageRepo.deleteById(id);
    }
}
