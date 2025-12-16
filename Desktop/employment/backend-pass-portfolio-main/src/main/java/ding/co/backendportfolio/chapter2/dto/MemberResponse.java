package ding.co.backendportfolio.chapter2.dto;

import ding.co.backendportfolio.chapter2.entity.Member;
import lombok.Data;

@Data
public class MemberResponse {
    private Long id;
    private String email;
    private String nickname;
    private String role;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.role = member.getRole().name();
    }
} 