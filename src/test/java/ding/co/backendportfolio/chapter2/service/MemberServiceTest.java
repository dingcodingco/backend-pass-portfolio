package ding.co.backendportfolio.chapter2.service;

import ding.co.backendportfolio.chapter2.dto.MemberLoginRequest;
import ding.co.backendportfolio.chapter2.dto.MemberResponse;
import ding.co.backendportfolio.chapter2.dto.MemberSignupRequest;
import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.entity.Role;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.chapter2.service.MemberService;
import ding.co.backendportfolio.global.error.CustomException;
import ding.co.backendportfolio.global.error.ErrorCode;
import ding.co.backendportfolio.global.error.exception.EntityNotFoundException;
import ding.co.backendportfolio.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("회원 가입 성공")
    void signupSuccess() {
        // given
        MemberSignupRequest request = new MemberSignupRequest("test@example.com", "password123", "테스트유저");
        given(memberRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");

        // when
        memberService.signup(request);

        // then
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 가입 실패 - 중복된 이메일")
    void signupFailDuplicateEmail() {
        // given
        MemberSignupRequest request = new MemberSignupRequest("test@example.com", "password123", "테스트유저");
        given(memberRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        MemberLoginRequest request = new MemberLoginRequest("test@example.com", "password123");
        Member member = Member.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();
        
        given(memberRepository.findByEmail(request.getEmail())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(request.getPassword(), member.getPassword())).willReturn(true);
        given(jwtTokenProvider.createToken(request.getEmail())).willReturn("token");

        // when
        String token = memberService.login(request);

        // then
        assertThat(token).isEqualTo("token");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void loginFailEmailNotFound() {
        // given
        MemberLoginRequest request = new MemberLoginRequest("test@example.com", "password123");
        given(memberRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("회원을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void loginFailInvalidPassword() {
        // given
        MemberLoginRequest request = new MemberLoginRequest("test@example.com", "wrongPassword");
        Member member = Member.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();
        
        given(memberRepository.findByEmail(request.getEmail())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(request.getPassword(), member.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    void getCurrentMemberSuccess() {
        // given
        String email = "test@example.com";
        Member member = Member.builder()
                .email(email)
                .password("encodedPassword")
                .nickname("테스트유저")
                .role(Role.USER)
                .build();
        
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));

        // when
        MemberResponse response = memberService.getCurrentMember(email);

        // then
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getNickname()).isEqualTo("테스트유저");
    }
} 