package com.ffdev.showmethephoto.auth.api;

import com.ffdev.showmethephoto.auth.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserMeResponse getMe(@AuthenticationPrincipal Jwt jwt) {
        return userService.getMe(jwt);
    }

}
