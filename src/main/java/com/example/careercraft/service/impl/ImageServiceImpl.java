package com.example.careercraft.service.impl;

import com.example.careercraft.exception.AppException;
import com.example.careercraft.service.ImageService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;


@Service
@Data
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Value("${file.allowed-extensions}")
    private List<String> allowedFileExtensions;




    public String saveImage(MultipartFile image) {
        String fileName = validateAndCleanFileName(Objects.requireNonNull(image.getOriginalFilename()));
        Path uploadPath = getUploadPath();
        createDirectoriesIfNotExists(uploadPath);
        Path filePath = uploadPath.resolve(fileName);

        copyFile(image, filePath);
        return fileName; // Возвращаем имя файла
    }

    String validateAndCleanFileName(String originalFilename) {
        String[] fileParts = originalFilename.split("\\.");
        String fileExtension = fileParts[fileParts.length - 1];

        if (!allowedFileExtensions.contains(fileExtension)) {
            throw new AppException(HttpStatus.BAD_REQUEST,"Invalid extension for file");
        }

        return StringUtils.cleanPath(Objects.requireNonNull(originalFilename));
    }

    Path getUploadPath() {
        return Paths.get("src/main/resources/static");
    }

    void createDirectoriesIfNotExists(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new AppException(HttpStatus.BAD_REQUEST,"Failed to create directories");
            }
        }
    }

    private void copyFile(MultipartFile image, Path filePath) {
        try {
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new AppException(HttpStatus.BAD_REQUEST,"Failed to copy file");
        }
    }
}
