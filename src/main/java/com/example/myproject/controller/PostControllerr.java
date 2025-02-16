package com.example.myproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.myproject.modal.Post;
import com.example.myproject.repo.PostRepo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "https://haj-umrah-services.netlify.app")
@RequestMapping("/api/images")
public class PostControllerr { // Corrected class name

    private static final String UPLOAD_DIR = "/app/uploads/"; // Absolute path

    @Autowired
    private PostRepo imageRepository;

    @Value("${image.base.url:https://haj-umrah-backend.onrender.com/uploads/}")
    private String baseUrl; // Dynamic base URL for production environments

    // ✅ Upload Image
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new UploadResponse("Failed to upload. No file selected."));
        }

        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs(); // Creates directories if they don't exist
            }

            // Generate a unique file name
            String fileName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destinationFile = new File(UPLOAD_DIR + fileName);
            file.transferTo(destinationFile);

            // Store image URL in database
            String imageUrl = baseUrl + fileName;
            Post image = new Post(imageUrl);
            imageRepository.save(image);

            return ResponseEntity.ok(new UploadResponse("Upload successful!", imageUrl));
        } catch (IOException e) {
            // Logging the exception (use a logger)
            return ResponseEntity.status(500).body(new UploadResponse("Upload failed!"));
        }
    }

    // ✅ Get all images
    @GetMapping("/all")
    public ResponseEntity<List<Post>> getAllImages() {
        return ResponseEntity.ok(imageRepository.findAll());
    }

    // ✅ Get a single image by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getImageById(@PathVariable String id) {
        Optional<Post> image = imageRepository.findById(id);
        return image.<ResponseEntity<Object>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404).body(new UploadResponse("Image not found!")));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UploadResponse> updateImage(@PathVariable String id, @RequestParam("image") MultipartFile file) {
        Optional<Post> image = imageRepository.findById(id);
        if (!image.isPresent()) {
            return ResponseEntity.status(404).body(new UploadResponse("Image not found!"));
        }

        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs(); // Creates directories if they don't exist
            }

            // Generate a unique file name
            String fileName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destinationFile = new File(UPLOAD_DIR + fileName);
            file.transferTo(destinationFile);

            // Delete the old image file if exists
            File oldFile = new File(UPLOAD_DIR + image.get().getImageUrl().substring(image.get().getImageUrl().lastIndexOf("/") + 1));
            if (oldFile.exists()) {
                oldFile.delete();
            }

            // Update the image URL in the database
            String imageUrl = baseUrl + fileName;
            Post imageToUpdate = image.get();
            imageToUpdate.setImageUrl(imageUrl);
            imageRepository.save(imageToUpdate);

            return ResponseEntity.ok(new UploadResponse("Image updated successfully!", imageUrl));
        } catch (IOException e) {
            // Logging the exception (use a logger)
            return ResponseEntity.status(500).body(new UploadResponse("Image update failed!"));
        }
    }

    // ✅ Delete an image by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<UploadResponse> deleteImage(@PathVariable String id) {
        Optional<Post> image = imageRepository.findById(id);

        if (image.isPresent()) {
            // Optional: Delete the file on disk as well
            File oldFile = new File(UPLOAD_DIR + image.get().getImageUrl().substring(image.get().getImageUrl().lastIndexOf("/") + 1));
            if (oldFile.exists()) {
                oldFile.delete();
            }

            imageRepository.deleteById(id);
            return ResponseEntity.ok(new UploadResponse("Image deleted successfully!"));
        } else {
            return ResponseEntity.status(404).body(new UploadResponse("Image not found!"));
        }
    }
}

// ✅ Helper class for responses
class UploadResponse {
    private String message;
    private String imageUrl;

    public UploadResponse(String message) {
        this.message = message;
    }

    public UploadResponse(String message, String imageUrl) {
        this.message = message;
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
