package com.sparta.sogonsogon.member.oauth.entity;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.MemberRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String membername;
    private String nickname;
    private String email;
    private MemberRoleEnum roleEnum;

    public static OAuthAttributes of(String registrationId,
                                     String memberNameAttributeName,
                                     Map<String, Object> attributes){

        return ofGoogle(memberNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String memberNameAttributeName,
                                            Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .membername((String) attributes.get("email"))
                .email((String) attributes.get("email"))
                .nickname((String) attributes.get("name"))
                .attributes(attributes)
                .nameAttributeKey(memberNameAttributeName)
                .build();
    }

    public Member toEntity(){
        return Member.builder()
                .membername(email)
                .email(email)
                .nickname(nickname)
                .role(MemberRoleEnum.SOCIAL)
                .build();
    }
}
