package com.ffdev.showmethephoto.auth;

import com.ffdev.showmethephoto.user.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Nested
    @DisplayName("회원가입")
    class SignUp {

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

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("로그인 토큰으로 내 정보를 조회")
        void getMe_withValidToken_returnsUserInfo() throws Exception {
            String email = "jwt-" + UUID.randomUUID() + "@example.com";
            String name = "테스트 사용자";
            String token = signupAndLogin(email, name);

            mockMvc.perform(get("/api/v1/users/me")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.email").value(email))
                    .andExpect(jsonPath("$.name").value(name));
        }

        @Test
        @DisplayName("토큰 없이 내 정보를 조회하면 401을 반환")
        void getMe_withoutToken_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/users/me"))
                    .andExpect(status().is(401));
        }

        @Test
        @DisplayName("잘못된 토큰으로 내 정보를 조회하면 401을 반환한다")
        void getMe_withInvalidToken_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/users/me")
                            .header("Authorization", "Bearer invalid-token"))
                    .andExpect(status().is(401));
        }

        private String signupAndLogin(String email, String name) throws Exception {
            String signupBody = """
                        {
                            "email" : "%s",
                            "password" : "password123!",
                            "name" : "%s"
                        }
                    """.formatted(email, name);

            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(signupBody))
                    .andExpect(status().isCreated());
            String loginBody = """
                    {
                        "email": "%s",
                        "password": "password123!"
                    }
                    """.formatted(email);
            MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginBody))
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andReturn();
            return JsonPath.read(
                    result.getResponse().getContentAsString(),
                    "$.accessToken"
            );
        }
    }
}
