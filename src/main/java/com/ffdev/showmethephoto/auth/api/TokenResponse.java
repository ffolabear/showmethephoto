package com.ffdev.showmethephoto.auth.api;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
