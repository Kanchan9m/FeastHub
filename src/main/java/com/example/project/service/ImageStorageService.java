
package com.example.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    public String saveRestaurantImage(MultipartFile image) {
        return saveImage(image, "uploads/restaurants");
    }

    public String saveMenuImage(MultipartFile image) {
        return saveImage(image, "uploads/menu");
    }

    public String saveImage(MultipartFile image, String directory) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        String originalName = image.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(
                    originalName.lastIndexOf(".")
            ).toLowerCase(Locale.ROOT);
        }

        if (!Set.of(".jpg", ".jpeg", ".png", ".webp")
                .contains(extension)) {
            throw new IllegalArgumentException(
                    "Only JPG, JPEG, PNG, and WebP images are allowed."
            );
        }

        try {
            Path uploadDirectory = Paths.get(directory);
            Files.createDirectories(uploadDirectory);

            String fileName = UUID.randomUUID() + extension;
            Path filePath = uploadDirectory.resolve(fileName);

            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/" + directory.replace("\\", "/") + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Could not save image", e);
        }
    }
}
