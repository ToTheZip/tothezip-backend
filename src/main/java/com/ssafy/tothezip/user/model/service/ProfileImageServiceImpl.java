package com.ssafy.tothezip.user.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfileImageServiceImpl implements ProfileImageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5MB

    // 허용 타입
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    @Override
    public String uploadProfileImage(MultipartFile file) {
        validateFile(file);

        // 확장자 결정(컨텐츠 타입 기반)
        String ext = extensionFromContentType(file.getContentType());

        // 저장 파일명은 UUID로만 생성
        String filename = UUID.randomUUID() + ext;

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path target = dir.resolve(filename).normalize();

        // 디렉토리 탈출 방지
        if (!target.startsWith(dir)) {
            throw new IllegalArgumentException("Invalid path");
        }

        try {
            Files.createDirectories(dir);

            // 매직바이트 검사(위조 방지)
            verifyMagicBytes(file);

            // 저장
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }

        // 서비스 URL 반환(정적 서빙 핸들러로 접근)
        return "/uploads/" + filename;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("File too large");
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported content type");
        }
    }

    private String extensionFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Unsupported content type");
        };
    }

    /**
     * Content-Type만 믿으면 안 돼서, 파일 헤더(시그니처)를 확인
     */
    private void verifyMagicBytes(MultipartFile file) throws IOException {
        byte[] header = new byte[12];
        try (InputStream in = file.getInputStream()) {
            int read = in.read(header);
            if (read < 8) throw new IllegalArgumentException("Invalid image");
        }

        String contentType = file.getContentType();

        if ("image/png".equals(contentType)) {
            // PNG signature: 89 50 4E 47 0D 0A 1A 0A
            byte[] png = {(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            for (int i = 0; i < png.length; i++) {
                if (header[i] != png[i]) throw new IllegalArgumentException("Invalid PNG");
            }
            return;
        }

        if ("image/jpeg".equals(contentType)) {
            // JPEG signature starts with FF D8
            if (header[0] != (byte)0xFF || header[1] != (byte)0xD8) {
                throw new IllegalArgumentException("Invalid JPEG");
            }
            return;
        }

        if ("image/webp".equals(contentType)) {
            // WEBP: "RIFF" .... "WEBP"
            if (!(header[0]=='R' && header[1]=='I' && header[2]=='F' && header[3]=='F')) {
                throw new IllegalArgumentException("Invalid WEBP");
            }
            if (!(header[8]=='W' && header[9]=='E' && header[10]=='B' && header[11]=='P')) {
                throw new IllegalArgumentException("Invalid WEBP");
            }
        }
    }
}
