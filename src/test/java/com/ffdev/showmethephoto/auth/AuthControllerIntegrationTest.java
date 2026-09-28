package com.ffdev.showmethephoto.auth;

import com.ffdev.showmethephoto.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("회원가입 요청에 성공하면 201 반환")
    void signup_success() throws Exception {
        String email = "signup-test@example.com";

        String requestBody = """
                    {
                        "email" : "%s",
                        "password" : "password123!",
                        "name" : "테스트 사용자"
                    }
                """.formatted(email);
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("테스트 사용자"));
        assertThat(userRepository.existsByEmail(email)).isTrue();
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 400 반환")
    void signup_duplicateEmail() throws Exception {
        String requestBody = """
                    {
                        "email" : "duplicate@example.com",
                        "password" : "password123!",
                        "name" : "테스트 사용자"
                    }
                """;
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 400을 반환")
    void signup_invalidEmail() throws Exception {
        String requestBody = """
                    {
                        "email" : "invalid-email",
                        "password" : "password123!",
                        "name" : "테스트 사용자"
                    }
                """;
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이름이 비어있으면 400을 반환")
    void signup_shortPassword() throws Exception {
        String requestBody = """
                    {
                        "email" : "invalid-email",
                        "password" : "1234",
                        "name" : "테스트 사용자"
                    }
                """;
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("이름이 비어있으면 400을 반환")
    void signup_blankName() throws Exception {
        String requestBody = """
                    {
                        "email" : "invalid-email",
                        "password" : "password123!",
                        "name" : ""
                    }
                """;
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

}
