package ding.co.backendportfolio.chapter2.service;

import ding.co.backendportfolio.chapter2.dto.MemberLoginRequest;
import ding.co.backendportfolio.chapter2.dto.MemberResponse;
import ding.co.backendportfolio.chapter2.dto.MemberSignupRequest;
import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.entity.Role;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.global.error.CustomException;
import ding.co.backendportfolio.global.error.exception.EntityNotFoundException;
import ding.co.backendportfolio.global.error.ErrorCode;
import ding.co.backendportfolio.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(MemberSignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

    public String login(MemberLoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.createToken(member.getEmail());
    }

    public MemberResponse getMyInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        
        return new MemberResponse(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getCurrentMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. E001"));
        
        log.info("Found member: {}", member);
        return new MemberResponse(member);
    }
} 