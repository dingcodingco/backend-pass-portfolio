package ding.co.backendportfolio.chapter2.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class MemberLoginRequest {
    private String email;
    private String password;
} 