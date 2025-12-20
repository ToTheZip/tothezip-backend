package com.ssafy.tothezip.user.model.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageService {
    String uploadProfileImage(MultipartFile file);
}
