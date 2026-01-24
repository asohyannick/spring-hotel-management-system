package com.hotelCare.hostelCare.service.storage;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hotelCare.hostelCare.exception.BadRequestException;
import com.hotelCare.hostelCare.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;
@Configuration
public class FileStorageConfig {

    @Bean
    public Cloudinary cloudinary(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    private String tryExtractCloudinaryPublicId(String url) {
        try {
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String afterUpload = url.substring(uploadIndex + "/upload/".length());

            // remove optional version segment v1234/
            if (afterUpload.startsWith("v")) {
                int slash = afterUpload.indexOf('/');
                if (slash > -1) afterUpload = afterUpload.substring(slash + 1);
            }

            // remove query string
            int q = afterUpload.indexOf('?');
            if (q > -1) afterUpload = afterUpload.substring(0, q);

            // remove extension
            int dot = afterUpload.lastIndexOf('.');
            if (dot > -1) afterUpload = afterUpload.substring(0, dot);

            return afterUpload;
        } catch (Exception e) {
            return null;
        }
    }

    @Bean
    public FileStorageService fileStorageService(Cloudinary cloudinary) {
        return new FileStorageService() {

            private final Path baseDir = Paths.get("uploads", "profiles");

            @Override
            public String uploadProfilePicture(MultipartFile file) {
                if (file == null || file.isEmpty()) {
                    throw new BadRequestException("Profile picture is required");
                }

                // 1) Save locally first
                String localUrl = saveLocally(file);

                // 2) Upload to Cloudinary
                try {
                    String publicId = "profile_" + UUID.randomUUID();

                    Map<?, ?> uploadResult = cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "folder", "hostelcare/profiles",
                                    "public_id", publicId,
                                    "resource_type", "image"
                            )
                    );

                    Object secureUrl = uploadResult.get("secure_url");
                    if (secureUrl == null) {
                        throw new NotFoundException("Cloudinary upload failed (secure_url missing)");
                    }
                    return secureUrl.toString();
                } catch (Exception e) {
                    deleteByUrl(localUrl);
                    throw new NotFoundException("Failed to upload profile picture to Cloudinary", e);
                }
            }

            @Override
            public void deleteByUrl(String url) {
                if (url == null || url.isBlank()) return;

                if (url.startsWith("/uploads/")) {
                    String relative = url.startsWith("/") ? url.substring(1) : url;
                    try {
                        Files.deleteIfExists(Paths.get(relative));
                    } catch (Exception ignored) {}
                    return;
                }

                if (url.contains("res.cloudinary.com")) {
                    try {
                        String publicId = tryExtractCloudinaryPublicId(url);
                        if (publicId != null) {
                            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
                        }
                    } catch (Exception ignored) {}
                }
            }

            private String saveLocally(MultipartFile file) {
                try {
                    Files.createDirectories(baseDir);

                    String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
                    String ext = "";
                    int dot = original.lastIndexOf('.');
                    if (dot > -1) ext = original.substring(dot);

                    String filename = "profile_" + UUID.randomUUID() + ext;
                    Path target = baseDir.resolve(filename);

                    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                    return "/uploads/profiles/" + filename;

                } catch (IOException e) {
                    throw new NotFoundException("Failed to upload profile picture locally", e);
                }
            }


        };
    }
}
