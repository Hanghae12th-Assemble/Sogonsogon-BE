package com.sparta.sogonsogon.member.oauth.dto;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UserRequestMapper {

    public MemberSessionDto toDto(OAuth2User oAuth2User){
        var attributes = oAuth2User.getAttributes();
        return MemberSessionDto.builder()
                .email((String) attributes.get("email"))
                .membername((String) attributes.get("id"))
                .nickname((String) attributes.get("name"))
                .profileImageUrl((String) attributes.get("profileImage"))
                .build();
    }

}
