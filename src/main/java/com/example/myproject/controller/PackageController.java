package com.example.myproject.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.myproject.modal.CardPackage;
import com.example.myproject.service.PackageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cards")
public class PackageController {

    private final PackageService packageService;
    private final Cloudinary cloudinary;

    // Constructor injection for PackageService and Cloudinary
    public PackageController(PackageService packageService,
                             @Value("${cloudinary.cloud-name}") String cloudName,
                             @Value("${cloudinary.api-key}") String apiKey,
                             @Value("${cloudinary.api-secret}") String apiSecret) {
        this.packageService = packageService;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    // Add a new card with image upload to Cloudinary
    @PostMapping("/add")
    public Map<String, String> uploadCard(
            @RequestParam("image") MultipartFile image,
            @RequestParam("packageName") String packageName,
            @RequestParam("price") String price,
            @RequestParam("start") String start,
            @RequestParam("hotel") String hotel,
            @RequestParam("ticket") String ticket,
            @RequestParam("transport") String transport,
            @RequestParam("ziyarathTour") String ziyarathTour,
            @RequestParam("guide") String guide,
            @RequestParam("meals") String meals,
            @RequestParam("kit") String kit,
            @RequestParam("assist") String assist,
            @RequestParam("visa") String visa) {

        try {
            // Call the service to save the card with Cloudinary image upload handling inside the service method
            packageService.addCard(image, packageName, price, start, hotel, ticket, transport, meals, ziyarathTour, guide, kit, assist, visa);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Card uploaded successfully!");
            return response;
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error uploading image: " + e.getMessage());
            return response;
        }
    }

    // Retrieve all cards
    @GetMapping("/all")
    public List<CardPackage> getAllCards() {
        return packageService.getAllCards();
    }

    // Retrieve a card by its ID
    @GetMapping("/{id}")
    public CardPackage getCardById(@PathVariable String id) {
        return packageService.getCardById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + id));
    }

    // Update a card by its ID with image upload to Cloudinary
    @PutMapping("/update/{id}")
    public Map<String, String> updateCard(
            @PathVariable String id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("packageName") String packageName,
            @RequestParam("price") String price,
            @RequestParam("start") String start,
            @RequestParam("hotel") String hotel,
            @RequestParam("ticket") String ticket,
            @RequestParam("transport") String transport,
            @RequestParam("ziyarathTour") String ziyarathTour,
            @RequestParam("guide") String guide,
            @RequestParam("meals") String meals,
            @RequestParam("kit") String kit,
            @RequestParam("assist") String assist,
            @RequestParam("visa") String visa) {

        try {
            // Call the service to update the card, passing the image (MultipartFile) if present
            packageService.updateCard(id, image, packageName, price, start, hotel, ticket, transport, meals, ziyarathTour, guide, kit, assist, visa);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Card updated successfully!");
            return response;
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error updating card: " + e.getMessage());
            return response;
        }
    }

    // Delete a card by its ID
    @DeleteMapping("/delete/{id}")
    public Map<String, String> deleteCard(@PathVariable String id) {
        packageService.deleteCard(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Card deleted successfully!");
        return response;
    }

    // Upload image to Cloudinary directly
    private String uploadImageToCloudinary(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
        
        // Extract and return the image URL
        return (String) uploadResult.get("secure_url");
    }
}
