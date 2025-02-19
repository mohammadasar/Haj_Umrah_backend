package com.example.myproject.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.myproject.modal.youtubeVideo;
import com.example.myproject.repo.youtubeVideoRepo;
import com.example.myproject.service.YoutubeVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "https://haj-umrah-services.netlify.app") 
@RestController
@RequestMapping("/api/videos")
public class YoutubeVideoController {

    private final youtubeVideoRepo videoRepository;
    private final YoutubeVideoService videoService;
    private final Cloudinary cloudinary;

    @Autowired
    public YoutubeVideoController(youtubeVideoRepo videoRepository, YoutubeVideoService videoService, Cloudinary cloudinary) {
        this.videoRepository = videoRepository;
        this.videoService = videoService;
        this.cloudinary = cloudinary;
    }

    /**
     * Get all video metadata.
     */
    @GetMapping("/get")
    public List<youtubeVideo> getAllVideos() {
        return videoRepository.findAll();
    }

    /**
     * Get a single video metadata by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoById(@PathVariable String id) {
        Optional<youtubeVideo> videoOptional = videoRepository.findById(id);
        if (videoOptional.isPresent()) {
            return ResponseEntity.ok(videoOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Video not found."));
        }
    }


    /**
     * Upload a video file to Cloudinary and save its metadata.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("video/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only video files are allowed."));
        }

        try {
            // Upload video to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), 
                ObjectUtils.asMap("resource_type", "video", "format", "mp4")
            );

            String cloudinaryUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id"); // Save this for deletion

            // Save video metadata in MongoDB
            youtubeVideo video = new youtubeVideo();
            video.setName(file.getOriginalFilename());
            video.setUrl(cloudinaryUrl);
            videoRepository.save(video);

            return ResponseEntity.ok(Map.of("message", "Video uploaded successfully.", "url", cloudinaryUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to upload video."));
        }
    }

    /**
     * Update a video file by ID (Delete old and Upload new video).
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateVideo(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("video/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only video files are allowed."));
        }

        try {
            Optional<youtubeVideo> videoOptional = videoRepository.findById(id);
            if (videoOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Video not found."));
            }

            youtubeVideo video = videoOptional.get();
            String oldVideoUrl = video.getUrl();

            if (oldVideoUrl != null && !oldVideoUrl.isBlank()) {
                // Extract public_id safely
                String publicId = extractPublicId(oldVideoUrl);
                if (publicId != null) {
                    // Delete old video from Cloudinary
                    Map<String, Object> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
                    System.out.println("Cloudinary delete result: " + deleteResult);
                }
            }

            // Upload new video to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "video", "format", "mp4"));
            String newCloudinaryUrl = (String) uploadResult.get("secure_url");

            // Update video metadata
            video.setUrl(newCloudinaryUrl);
            video.setName(file.getOriginalFilename());
            videoRepository.save(video);

            return ResponseEntity.ok(Map.of("message", "Video updated successfully.", "url", newCloudinaryUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error updating video.", "details", e.getMessage()));
        }
    }


    /**
     * Delete a video by ID.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteVideo(@PathVariable String id) {
        Optional<youtubeVideo> videoOptional = videoRepository.findById(id);

        if (videoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Video not found."));
        }

        youtubeVideo video = videoOptional.get();
        String videoUrl = video.getUrl();

        if (videoUrl == null || videoUrl.isBlank()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Video URL is missing."));
        }

        try {
            // Extract public_id safely using regex
            String publicId = extractPublicId(videoUrl);
            if (publicId == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Invalid video URL format."));
            }

            // Delete video from Cloudinary
            Map<String, Object> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
            System.out.println("Cloudinary delete result: " + deleteResult);

            if (!"ok".equals(deleteResult.get("result"))) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Cloudinary deletion failed."));
            }

            // Delete from database
            videoRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Video deleted successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete video.", "details", e.getMessage()));
        }
    }
    
    private String extractPublicId(String videoUrl) {
        try {
            String regex = ".*/upload/(v\\d+/)?([^/.]+)\\..*";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(videoUrl);

            if (matcher.matches()) {
                return matcher.group(2); // Extracts public_id correctly
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if extraction fails
    }


}
