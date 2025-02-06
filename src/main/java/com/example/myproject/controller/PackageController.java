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

        packageService.addCard(image,packageName, price, start, hotel, ticket, transport, meals, ziyarathTour, guide, kit, assist,visa);

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

        packageService.updateCard(id, image, packageName, price, start, hotel, ticket, transport, meals, ziyarathTour, guide, kit, assist, visa);

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
