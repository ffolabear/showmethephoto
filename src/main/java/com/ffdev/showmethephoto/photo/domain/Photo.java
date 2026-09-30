package com.ffdev.showmethephoto.photo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @Getter
    private UUID id;
    @Column(name = "user_id", nullable = false)
    @Getter
    private UUID userId;
    @Column(name = "original_filename", nullable = false)
    @Getter
    private String originalFilename;
    @Column(name = "storage_key", nullable = false, unique = true)
    @Getter
    private String storageKey;
    @Column(name = "content_type", nullable = false)
    @Getter
    private String contentType;
    @Column(name = "file_size", nullable = false)
    @Getter
    private long fileSize;
    @Column(name = "created_at", nullable = false)
    @Getter
    private Instant createdAt;

    private Photo(
            UUID userId,
            String originalFilename,
            String storageKey,
            String contentType,
            long fileSize
    ) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.originalFilename = originalFilename;
        this.storageKey = storageKey;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.createdAt = Instant.now();
    }

    public static Photo create(
            UUID userId,
            String originalFilename,
            String storageKey,
            String contentType,
            long fileSize
    ) {
        return new Photo(
                userId,
                originalFilename,
                storageKey,
                contentType,
                fileSize
        );
    }
}
