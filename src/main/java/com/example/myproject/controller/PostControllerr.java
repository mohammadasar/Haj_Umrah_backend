package com.example.myproject.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.myproject.modal.Post;
import com.example.myproject.repo.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "https://haj-umrah-services.netlify.app")
@RequestMapping("/api/images")
public class PostControllerr {

    @Autowired
    private PostRepo imageRepository;

    @Value("${image.base.url:https://haj-umrah-backend.onrender.com/uploads/}")
    private String baseUrl; // Dynamic base URL for production environments

    private final Cloudinary cloudinary;

    @Autowired
    public PostControllerr(PostRepo imageRepository, Cloudinary cloudinary) {
        this.imageRepository = imageRepository;
        this.cloudinary = cloudinary;
    }

    // ✅ Upload Image to Cloudinary
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new UploadResponse("Failed to upload. No file selected."));
        }

        try {
            // Upload image to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            // Retrieve the URL from Cloudinary response
            String imageUrl = (String) uploadResult.get("url");

            // Store image URL in database
            Post image = new Post(imageUrl);
            imageRepository.save(image);

            return ResponseEntity.ok(new UploadResponse("Upload successful!", imageUrl));
        } catch (IOException e) {
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

    // ✅ Update Image (Upload to Cloudinary)
    @PutMapping("/update/{id}")
    public ResponseEntity<UploadResponse> updateImage(@PathVariable String id, @RequestParam("image") MultipartFile file) {
        Optional<Post> image = imageRepository.findById(id);
        if (!image.isPresent()) {
            return ResponseEntity.status(404).body(new UploadResponse("Image not found!"));
        }

        try {
            // Upload the new image to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("url");

            // Update the image URL in the database
            Post imageToUpdate = image.get();
            imageToUpdate.setImageUrl(imageUrl);
            imageRepository.save(imageToUpdate);

            return ResponseEntity.ok(new UploadResponse("Image updated successfully!", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new UploadResponse("Image update failed!"));
        }
    }

    // ✅ Delete an image by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<UploadResponse> deleteImage(@PathVariable String id) {
        Optional<Post> image = imageRepository.findById(id);

        if (image.isPresent()) {
            // Delete the image metadata from the database
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
