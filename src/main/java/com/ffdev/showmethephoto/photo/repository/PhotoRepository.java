package com.ffdev.showmethephoto.photo.repository;

import com.ffdev.showmethephoto.photo.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {

    List<Photo> findAllByUserIdOrderByCreatedAtDesc(UUID uuid);

}