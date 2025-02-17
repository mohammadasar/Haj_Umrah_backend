package com.example.myproject.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.myproject.modal.CardPackage;
import com.example.myproject.repo.PackageRepo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PackageService {

    private final PackageRepo packageRepo;

    private final Cloudinary cloudinary;

    // Constructor injection for Cloudinary configuration
    public PackageService(PackageRepo packageRepo, 
                          @Value("${cloudinary.cloud-name}") String cloudName,
                          @Value("${cloudinary.api-key}") String apiKey,
                          @Value("${cloudinary.api-secret}") String apiSecret) {
        this.packageRepo = packageRepo;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    // Add a new card
    public CardPackage addCard(MultipartFile image, String packageName, String price, String start, 
                               String hotel, String ticket, String transport, String meals, 
                               String ziyarathTour, String guide, String kit, String assist, String visa) {

        // Upload image to Cloudinary
        String imageUrl = uploadImageToCloudinary(image);

        // Create and save a new CardPackage object with Cloudinary image URL
        CardPackage cardPackage = new CardPackage(imageUrl, packageName, price, start, hotel, ticket, transport, meals, ziyarathTour, guide, kit, assist, visa);
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
    public CardPackage updateCard(String id, MultipartFile image, String packageName, String price, 
                                  String start, String hotel, String ticket, String transport, 
                                  String meals, String ziyarathTour, String guide, String kit, 
                                  String assist, String visa) {

        // Fetch the existing CardPackage
        CardPackage cardPackage = packageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + id));

        // If an image is provided, upload it to Cloudinary and update the file URL
        if (image != null && !image.isEmpty()) {
            String imageUrl = uploadImageToCloudinary(image);
            cardPackage.setImage(imageUrl);
        }

        // Update other fields
        cardPackage.setPackageName(packageName);
        cardPackage.setPrice(price);
        cardPackage.setStart(start);
        cardPackage.setHotel(hotel);
        cardPackage.setTicket(ticket);
        cardPackage.setTransport(transport);
        cardPackage.setMeals(meals);
        cardPackage.setZiyarathTour(ziyarathTour);
        cardPackage.setGuide(guide);
        cardPackage.setKit(kit);
        cardPackage.setAssist(assist);
        cardPackage.setVisa(visa);

        return packageRepo.save(cardPackage);
    }

    // Delete a card by its ID
    public void deleteCard(String id) {
        if (!packageRepo.existsById(id)) {
            throw new RuntimeException("Card not found with ID: " + id);
        }
        packageRepo.deleteById(id);
    }

    // Upload image to Cloudinary and return the image URL
    private String uploadImageToCloudinary(MultipartFile file) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }
}
