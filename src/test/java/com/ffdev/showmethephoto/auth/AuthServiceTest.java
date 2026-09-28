package com.ffdev.showmethephoto.auth;

import com.ffdev.showmethephoto.auth.api.SignupRequest;
import com.ffdev.showmethephoto.auth.api.SignupResponse;
import com.ffdev.showmethephoto.auth.application.AuthService;
import com.ffdev.showmethephoto.auth.application.EmailAlreadyExistsException;
import com.ffdev.showmethephoto.user.domain.User;
import com.ffdev.showmethephoto.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository, passwordEncoder
        );
    }

    @Test
    @DisplayName("정상 회원가입")
    void signup_success() {
        //given
        SignupRequest request = new SignupRequest(
                "test@example.com",
                "password123!",
                "테스트 사용자"
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("password123!"))
                .thenReturn("encoded-password");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        SignupResponse response = authService.signup(request);

        // then
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.name()).isEqualTo("테스트 사용자");
        assertThat(response.id()).isNotNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getName()).isEqualTo("테스트 사용자");
        assertThat(savedUser.getPasswordHash()).isEqualTo("encoded-password");

//        assertThat(savedUser.getCreatedAt()).isNotNull();
//        assertThat(savedUser.getUpdatedAt()).isNotNull();

        verify(passwordEncoder).encode("password123!");
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 가입하면 예외 발생")
    void signup_duplicateEmail() {
        //given
        SignupRequest request = new SignupRequest(
                "test@example.com",
                "password123!",
                "테스트 사용자"
        );
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        //when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository).existsByEmail("test@example.com");
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }


}
