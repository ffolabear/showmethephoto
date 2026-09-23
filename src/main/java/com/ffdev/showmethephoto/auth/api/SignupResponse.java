package com.ffdev.showmethephoto.auth.api;

import com.ffdev.showmethephoto.user.domain.User;

import java.util.UUID;

public record SignupResponse(
        UUID id,
        String email,
        String name
) {

    public static SignupResponse from(User user) {
        return new SignupResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}
