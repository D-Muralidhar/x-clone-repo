package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MediaService {

    /**
     * Save file somewhere (local, S3, Cloudinary) and return public URL.
     */
    String saveFile(MultipartFile file) throws IOException;
}
