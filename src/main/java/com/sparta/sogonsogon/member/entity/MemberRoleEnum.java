package com.sparta.sogonsogon.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRoleEnum {

    USER("ROLE_USER"),  // 사용자 권한
    ADMIN("ROLE_ADMIN"),  // 관리자 권한
    SOCIAL("ROLE_SOCIAL")// 소셜 로그인 회원
    ;

    private final String value;

//    private final String authority;
//
//    MemberRoleEnum(String authority) {
//        this.authority = authority;
//    }
//
//    public String getAuthority() {
//        return this.authority;
//    }
//
//    public static class Authority {
//        public static final String USER = "ROLE_USER";
//        public static final String ADMIN = "ROLE_ADMIN";
////        public static final String SOCIAL = "ROLE_SOCIAL";
//    }
}
