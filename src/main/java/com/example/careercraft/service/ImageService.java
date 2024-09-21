package com.example.careercraft.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    public String saveImage(MultipartFile image);
}
