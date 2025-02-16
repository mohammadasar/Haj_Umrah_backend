package com.example.myproject.controller;

import com.example.myproject.modal.youtubeVideo;
import com.example.myproject.repo.youtubeVideoRepo;
import com.example.myproject.service.YoutubeVideoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "https://haj-umrah-services.netlify.app")  // Allow all origins (for testing)
@RestController
@RequestMapping("/api/videos")
public class YoutubeVideoController {

    @Value("${video.upload.path}")
    private String uploadPath; // ✅ Use application.properties for path

    private final youtubeVideoRepo videoRepository;
    private final YoutubeVideoService videoService;

    public YoutubeVideoController(youtubeVideoRepo videoRepository, YoutubeVideoService videoService) {
        this.videoRepository = videoRepository;
        this.videoService = videoService;
    }

    /**
     * Get all video metadata.
     */
    @GetMapping("/get")
    public List<youtubeVideo> getAllVideos() {
        return videoRepository.findAll();
    }

    /**
     * Sanitize the file name to remove illegal characters.
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[<>:\"/\\\\|?*]", "_");
    }

    /**
     * Get the full file path for the given file name.
     */
    private Path getFilePath(String fileName) {
        try {
            fileName = sanitizeFileName(fileName);
            return Paths.get(uploadPath, fileName).normalize();
        } catch (InvalidPathException e) {
            throw new RuntimeException("Invalid file path: " + e.getMessage(), e);
        }
    }

    /**
     * Serve a video file by its file name.
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getVideo(@PathVariable String fileName) {
        try {
            Path filePath = getFilePath(fileName);
            File file = filePath.toFile(); // Convert Path to File

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            // ✅ Convert to URL safely
            Resource resource = new UrlResource(file.toURI());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath); // Detect MIME type

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "application/octet-stream")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    /**
     * Upload a video file and save its metadata.
     */
    @PostMapping
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is required.");
        }

        try {
            String fileName = UUID.randomUUID() + "_" + sanitizeFileName(file.getOriginalFilename());
            Path destination = Paths.get(uploadPath, fileName);

            // ✅ Ensure directory exists
            Files.createDirectories(destination.getParent());

            // ✅ Save the file
            file.transferTo(destination.toFile());

            // ✅ Save metadata in MongoDB
            youtubeVideo video = new youtubeVideo();
            video.setName(file.getOriginalFilename());
            video.setUrl(fileName);  // ✅ Store file name only, not full path
            videoRepository.save(video);

            return ResponseEntity.ok("Video uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload video: " + e.getMessage());
        }
    }

    /**
     * Update video file by ID.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateVideo(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is required.");
        }

        try {
            boolean updated = videoService.updateVideo(id, file);
            if (updated) {
                return ResponseEntity.ok("Video updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating video.");
        }
    }

    /**
     * Delete a video file and its metadata by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable String id) {
        Optional<youtubeVideo> videoOptional = videoRepository.findById(id);

        if (videoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found.");
        }

        youtubeVideo video = videoOptional.get();
        Path filePath = getFilePath(video.getUrl()); // ✅ Get correct file path

        try {
            Files.deleteIfExists(filePath); // ✅ Delete file safely

            videoRepository.deleteById(id);
            return ResponseEntity.ok("Video deleted successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete video: " + e.getMessage());
        }
    }
}
