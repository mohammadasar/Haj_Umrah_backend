package com.example.myproject.service;

import com.example.myproject.modal.youtubeVideo;
import com.example.myproject.repo.youtubeVideoRepo;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class YoutubeVideoService {

    @Value("${video.upload.path}")
    private String uploadPath;

    private final youtubeVideoRepo videoRepository;

    public YoutubeVideoService(youtubeVideoRepo videoRepository) {
        this.videoRepository = videoRepository;
    }

    // Fetch all videos
    public List<youtubeVideo> getAllVideos() {
        return videoRepository.findAll();
    }

    // Upload a new video
    public String uploadVideo(MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty. Please upload a valid file.";
        }

        // ✅ Validate file type (optional)
        String contentType = file.getContentType();
        if (!contentType.startsWith("video")) {
            return "Only video files are allowed.";
        }

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = Paths.get(uploadPath, fileName);

            // ✅ Ensure directory exists
            File uploadFolder = new File(uploadPath);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            // ✅ Transfer file safely
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // ✅ Save video details to MongoDB
            youtubeVideo video = new youtubeVideo();
            video.setName(file.getOriginalFilename());
            video.setUrl(destination.toString()); // Store absolute file path
            videoRepository.save(video);

            return "Video uploaded successfully";
        } catch (IOException e) {
            return "Error uploading video: " + e.getMessage();
        }
    }

    // Update video file
    public boolean updateVideo(ObjectId id, MultipartFile file) {
        if (file.isEmpty()) {
            return false; // No file provided
        }

        Optional<youtubeVideo> optionalVideo = videoRepository.findById(id);
        if (optionalVideo.isEmpty()) {
            return false; // Video not found
        }

        youtubeVideo video = optionalVideo.get();

        try {
            // ✅ Delete old file if exists
            Path oldFilePath = Paths.get(video.getUrl());
            Files.deleteIfExists(oldFilePath);

            // ✅ Save new file
            String newFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path newFilePath = Paths.get(uploadPath, newFileName);
            Files.copy(file.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

            // ✅ Update DB with new file URL
            video.setUrl(newFilePath.toString());
            videoRepository.save(video);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a video by ID
    public String deleteVideo(ObjectId id) {
        Optional<youtubeVideo> videoOptional = videoRepository.findById(id);

        if (videoOptional.isEmpty()) {
            return "Video not found";
        }

        youtubeVideo video = videoOptional.get();

        try {
            // ✅ Delete physical file
            Path fileToDelete = Paths.get(video.getUrl());
            Files.deleteIfExists(fileToDelete);

            // ✅ Remove from DB
            videoRepository.deleteById(id);
            return "Video deleted successfully";
        } catch (IOException e) {
            return "Failed to delete video: " + e.getMessage();
        }
    }
}
