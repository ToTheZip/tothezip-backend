package com.ssafy.tothezip.user.model;

import lombok.Data;

@Data
public class EmailVerifyRequestDto {
    private String email;
    private String code;  // 사용자가 입력한 인증코드
}
