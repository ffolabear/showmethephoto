package com.ffdev.showmethephoto.auth.application;

import com.ffdev.showmethephoto.auth.api.UserMeResponse;
import com.ffdev.showmethephoto.user.domain.User;
import com.ffdev.showmethephoto.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserMeResponse getMe(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다"
                ));
        return new UserMeResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

}
