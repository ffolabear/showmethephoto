package com.ffdev.showmethephoto.auth.api;

import java.util.UUID;

public record UserMeResponse(
        UUID id,
        String email,
        String name
) {
}
