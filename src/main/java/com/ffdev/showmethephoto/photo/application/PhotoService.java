package com.ffdev.showmethephoto.photo.application;

import com.ffdev.showmethephoto.photo.api.PhotoResponse;
import com.ffdev.showmethephoto.photo.domain.Photo;
import com.ffdev.showmethephoto.photo.repository.PhotoRepository;
import com.ffdev.showmethephoto.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
public class PhotoService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final Path photoDirectory;

    public PhotoService(PhotoRepository photoRepository, UserRepository userRepository, @Value("${app.storage.photo-dir}") String photoDirectory) {
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
        this.photoDirectory = Paths.get(photoDirectory).toAbsolutePath().normalize();
    }

    public PhotoResponse upload(UUID userId, MultipartFile file) throws IOException {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "사용자를 찾을 수 없습니다."
                ));
        validateImage(file);
        String contentType = file.getContentType();
        String storageKey = UUID.randomUUID() + EXTENSIONS.get(contentType);
        Path target = photoDirectory.resolve(storageKey).normalize();

        if (!target.startsWith(photoDirectory)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "잘못된 파일 경로입니다."
            );
        }

        try {
            Files.createDirectories(photoDirectory);
            Files.copy(file.getInputStream(), target);

            Photo photo = Photo.create(
                    userId,
                    file.getOriginalFilename() == null
                            ? "unamed"
                            : file.getOriginalFilename(),
                    storageKey,
                    contentType,
                    file.getSize()
            );
            Photo savedPhoto = photoRepository.saveAndFlush(photo);
            return PhotoResponse.from(savedPhoto);
        } catch (IOException e) {
            deleteFileIfExists(target);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "파일 저장에 실패했습니다.",
                    e
            );
        } catch (RuntimeException e) {
            deleteFileIfExists(target);
            throw e;
        }
    }

    private void deleteFileIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    private void validateImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "업로드할 파일을 선택하세요."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "파일 크기는 50MB 이하여야 합니다."
            );
        }

        String contentType = file.getContentType();

        if (!EXTENSIONS.containsKey(contentType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "JPEG 또는 PNG 파일만 업로드할 수 있습니다."
            );
        }

        try (InputStream inputStream = file.getInputStream()) {
            if (ImageIO.read(inputStream) == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "올바른 이미지 파일이 아닙니다."
                );
            }
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미지 파일을 읽을 수 없습니다."
            );
        }
    }
}
