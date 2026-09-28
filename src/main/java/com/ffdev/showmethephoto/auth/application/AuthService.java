package com.ffdev.showmethephoto.auth.application;

import com.ffdev.showmethephoto.auth.api.LoginRequest;
import com.ffdev.showmethephoto.auth.api.SignupRequest;
import com.ffdev.showmethephoto.auth.api.SignupResponse;
import com.ffdev.showmethephoto.auth.api.TokenResponse;
import com.ffdev.showmethephoto.auth.exception.InvalidCredentialException;
import com.ffdev.showmethephoto.user.domain.User;
import com.ffdev.showmethephoto.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String email = normalizerEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        String passwordHash = passwordEncoder.encode(
                request.password()
        );

        User user = User.create(
                email, passwordHash, request.name().trim()
        );

        User savedUser = userRepository.save(user);

        return SignupResponse.from(savedUser);
    }

    public TokenResponse login(LoginRequest request) {
        String normalizedEmail = normalizerEmail(request.email());
        User user = userRepository.findByEmail(normalizedEmail).orElseThrow(InvalidCredentialException::new);
        if (!passwordEncoder.matches(
                request.password(), user.getPasswordHash()
        )) {
            throw new InvalidCredentialException();
        }

        String accessToken = tokenService.createAccessToken(user);

        return new TokenResponse(
                accessToken,
                "Bearer",
                3600
        );
    }


    private String normalizerEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

}
