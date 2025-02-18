package com.example.myproject.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.myproject.modal.youtubeVideo;
import com.example.myproject.repo.youtubeVideoRepo;
import com.example.myproject.service.YoutubeVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "https://haj-umrah-services.netlify.app")  // Allow all origins (for testing)
@RestController
@RequestMapping("/api/videos")
public class YoutubeVideoController {

    @Value("${video.upload.path}")
    private String uploadPath;

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
     * Upload a video file to Cloudinary and save its metadata.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is required."));
        }

        try {
            // Upload video to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "video"));
            String cloudinaryUrl = (String) uploadResult.get("url");

            // Save video metadata in MongoDB
            youtubeVideo video = new youtubeVideo();
            video.setName(file.getOriginalFilename());
            video.setUrl(cloudinaryUrl);
            videoRepository.save(video);

            // Return response
            return ResponseEntity.ok(Map.of(
                "message", "Video uploaded successfully.",
                "url", cloudinaryUrl
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to upload video."));
        }
    }


    /**
     * Update video file by ID (Upload to Cloudinary).
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateVideo(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is required.");
        }

        try {
            Optional<youtubeVideo> videoOptional = videoRepository.findById(id);
            if (videoOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found.");
            }

            youtubeVideo video = videoOptional.get();

            // Extract public_id from old Cloudinary URL
            String oldVideoUrl = video.getUrl();
            String publicId = oldVideoUrl.substring(oldVideoUrl.lastIndexOf("/") + 1, oldVideoUrl.lastIndexOf(".")); // Extract public_id

            // Delete old video from Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));

            // Upload new video
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "video"));
            String cloudinaryUrl = (String) uploadResult.get("url");

            // Update MongoDB record
            video.setUrl(cloudinaryUrl);
            videoRepository.save(video);

            return ResponseEntity.ok("Video updated successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating video.");
        }
    }


    /**
     * Delete a video by ID (Remove video metadata from MongoDB).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable String id) {
        Optional<youtubeVideo> videoOptional = videoRepository.findById(id);

        if (videoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found.");
        }

        youtubeVideo video = videoOptional.get();
        String videoUrl = video.getUrl();

        try {
            // Extract public_id from Cloudinary URL
            String publicId = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.lastIndexOf(".")); // Extract public_id

            // Delete video from Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));

            // Delete video metadata from MongoDB
            videoRepository.deleteById(id);

            return ResponseEntity.ok("Video deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete video.");
        }
    }

}
