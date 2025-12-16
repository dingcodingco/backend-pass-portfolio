package ding.co.backendportfolio.chapter2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ding.co.backendportfolio.chapter2.dto.MemberLoginRequest;
import ding.co.backendportfolio.chapter2.dto.MemberSignupRequest;
import ding.co.backendportfolio.chapter2.service.MemberService;
import ding.co.backendportfolio.config.TestConfig;
import ding.co.backendportfolio.config.TestDatabaseConfig;
import ding.co.backendportfolio.config.TestMeterRegistryConfig;
import ding.co.backendportfolio.config.TestSecurityConfig;
import ding.co.backendportfolio.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
@Import({TestConfig.class, TestSecurityConfig.class, TestMeterRegistryConfig.class, TestDatabaseConfig.class})
@AutoConfigureDataJpa
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("회원 가입 API")
    void signup() throws Exception {
        // given
        MemberSignupRequest request = new MemberSignupRequest("test@example.com", "password123", "테스트유저");

        // when & then
        mockMvc.perform(post("/api/chapter2/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 API")
    void login() throws Exception {
        // given
        MemberLoginRequest request = new MemberLoginRequest("test@example.com", "password123");
        given(memberService.login(any())).willReturn("token");

        // when & then
        mockMvc.perform(post("/api/chapter2/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("회원 정보 조회 API")
    void getCurrentMember() throws Exception {
        // when & then
        mockMvc.perform(get("/api/chapter2/members/me"))
                .andExpect(status().isOk());
    }
} 