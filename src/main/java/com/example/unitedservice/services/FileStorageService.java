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
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {
            Path attachmentDir = Paths.get(uploadDir, "attachments").toAbsolutePath().normalize();
            Files.createDirectories(attachmentDir);

            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = "";

            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex >= 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }

            String fileName = "attachment_" + System.currentTimeMillis() + fileExtension;

            Path target = attachmentDir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store attachment file. Please try again!", e);
        }
    }

    public Resource loadFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, "attachments").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Attachment not found: " + fileName);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not load attachment file: " + fileName, ex);
        }
    }
}