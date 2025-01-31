package com.example.myproject.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.myproject.modal.CardPackage;
import com.example.myproject.service.PackageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cards")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    // Add a new card
    @PostMapping("/add")
    public Map<String, String> uploadCard(
            @RequestParam("image") MultipartFile image,
            @RequestParam("title1") String title1,
            @RequestParam("title2") String title2,
            @RequestParam("title3") String title3,
            @RequestParam("title4") String title4,
            @RequestParam("title5") String title5) {

        packageService.addCard(image, title1, title2, title3, title4, title5);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Card uploaded successfully!");
        return response;
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

    // Update a card by its ID
    @PutMapping("/update/{id}")
    public Map<String, String> updateCard(
            @PathVariable String id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("title1") String title1,
            @RequestParam("title2") String title2,
            @RequestParam("title3") String title3,
            @RequestParam("title4") String title4,
            @RequestParam("title5") String title5) {

        packageService.updateCard(id, image, title1, title2, title3, title4, title5);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Card updated successfully!");
        return response;
    }

    // Delete a card by its ID
    @DeleteMapping("/delete/{id}")
    public Map<String, String> deleteCard(@PathVariable String id) {
        packageService.deleteCard(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Card deleted successfully!");
        return response;
    }
}
