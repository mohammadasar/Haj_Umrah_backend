package com.example.myproject.controller;


import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private static final String UPLOADS_DIR = "/app/uploads"; // 🔹 Adjust if necessary

    @GetMapping("/list-files")
    public List<String> listFiles() {
        File folder = new File(UPLOADS_DIR);
        if (!folder.exists() || !folder.isDirectory()) {
            return List.of("Uploads directory does not exist or is not a directory.");
        }
        return Arrays.stream(folder.listFiles())
                .map(File::getName)
                .collect(Collectors.toList());
    }
}

