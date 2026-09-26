package com.example.project.service;

import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImageStorageService {
    private final Path uploadDirectory =
            Paths.get("uploads/restaurants");

    public String saveImage(MultipartFile image) {

        try {

            Files.createDirectories(uploadDirectory);

            String originalName =
                    image.getOriginalFilename();

            String extension = "";

            if (originalName != null &&
                    originalName.contains(".")) {

                extension =
                        originalName.substring(
                                originalName.lastIndexOf(".")
                        );
            }

            String fileName =
                    UUID.randomUUID() + extension;

            Path filePath =
                    uploadDirectory.resolve(fileName);

            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/uploads/restaurants/" + fileName;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not save image",
                    e
            );
        }
    }
}
