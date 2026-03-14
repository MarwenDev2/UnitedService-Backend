package com.example.unitedservice.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ImageStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {
            Path photoDir = Paths.get(uploadDir, "photos").toAbsolutePath().normalize();
            Files.createDirectories(photoDir);

            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = "profile_" + System.currentTimeMillis() + fileExtension;

            Path target = photoDir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    public Resource loadFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, "photos").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) return resource;
            throw new RuntimeException("File not found: " + fileName);
        } catch (Exception e) {
            throw new RuntimeException("Could not load file.", e);
        }
    }
}