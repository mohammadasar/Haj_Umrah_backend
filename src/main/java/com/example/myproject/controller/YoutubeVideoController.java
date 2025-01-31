package com.example.myproject.controller;

import com.example.myproject.modal.youtubeVideo;
import com.example.myproject.repo.youtubeVideoRepo;
import com.example.myproject.service.YoutubeVideoService;

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
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@CrossOrigin(origins = "*")  // Allow all origins (for testing)
@RestController
@RequestMapping("/api/videos")
public class YoutubeVideoController {

    private final String uploadPath = "D:/my_own_projects/backend_projects/myproject/youtubeVideos/"; // Hardcoded upload path
    private final youtubeVideoRepo videoRepository;
    private final YoutubeVideoService videoService; //  Inject service

 // ✅ Constructor Injection
    public YoutubeVideoController(youtubeVideoRepo videoRepository, YoutubeVideoService videoService) {
        this.videoRepository = videoRepository;
        this.videoService = videoService; // ✅ Initialize service
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
        // Replace illegal characters with underscores
        return fileName.replaceAll("[<>:\"/\\\\|?*]", "_");
    }

    /**
     * Get the full file path for the given file name.
     */
    public Path getFilePath(String fileName) {
        try {
            // Sanitize the file name to prevent path traversal attacks
            fileName = sanitizeFileName(fileName);

            // Construct and return the full path
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
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Upload a video file and save its metadata.
     */
    @PostMapping
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + sanitizeFileName(file.getOriginalFilename());
        File destination = new File(uploadPath + fileName);

        try {
            // Save the file to the server
            file.transferTo(destination);

            // Save video metadata to the database
            youtubeVideo video = new youtubeVideo();
            video.setName(file.getOriginalFilename());
            video.setUrl("api/videos/" + fileName);  // Remove the extra slash
            videoRepository.save(video);

            System.out.println("Uploaded file: " + fileName);
            return ResponseEntity.ok("Video uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload video");
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
            boolean updated = videoService.updateVideo(id, file); // Now the ID type matches
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
        if (videoRepository.existsById(id)) {
            youtubeVideo video = videoRepository.findById(id).orElse(null);

            if (video != null) {
                // Delete the video file from the server
                File file = new File(uploadPath + video.getUrl().replace("/api/videos/", ""));
                if (file.exists() && file.delete()) {
                    System.out.println("Deleted file: " + file.getName());
                }

                // Remove the video metadata from the database
                videoRepository.deleteById(id);
                return ResponseEntity.ok("Video deleted successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found");
    }
}
