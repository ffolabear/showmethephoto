package com.ffdev.showmethephoto.photo.api;

import com.ffdev.showmethephoto.photo.domain.Photo;

import java.time.Instant;
import java.util.UUID;

public record PhotoResponse(
        UUID id,
        String originalFilename,
        String contentType,
        long fileSize,
        Instant createdAt
) {

    public static PhotoResponse from(Photo photo) {
        return new PhotoResponse(
                photo.getId(),
                photo.getOriginalFilename(),
                photo.getContentType(),
                photo.getFileSize(),
                photo.getCreatedAt()
        );
    }
}
