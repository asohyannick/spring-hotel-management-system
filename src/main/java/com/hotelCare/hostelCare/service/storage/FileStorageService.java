package com.hotelCare.hostelCare.service.storage;
import org.springframework.web.multipart.MultipartFile;
public interface FileStorageService {
    String uploadProfilePicture(MultipartFile file);
    void deleteByUrl(String url);
}
