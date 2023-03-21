package com.sparta.sogonsogon.member.oauth.entity;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.MemberRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Slf4j
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String membername;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private MemberRoleEnum roleEnum;

    public static OAuthAttributes of(String registrationId, String memberNameAttributeName, Map<String, Object> attributes){

        if ("naver".equals(registrationId)) {
                return ofNaver("id", attributes);
        } else if (registrationId.equals("kako")) {
            return ofKakao(memberNameAttributeName, attributes);
        }
        return ofGoogle(memberNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String memberNameAttributeName,
                                            Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .membername((String) attributes.get("sub"))
                .email((String) attributes.get("email"))
                .nickname((String) attributes.get("name"))
                .profileImageUrl((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(memberNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        /* JSON형태이기 때문에 Map을 통해 데이터를 가져온다. */
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        log.info("naver response : " + response);

        return OAuthAttributes.builder()
        .membername((String) response.get("email"))
        .email((String) response.get("email"))
        .nickname((String) response.get("nickname"))
        .profileImageUrl((String) response.get("picture"))
        .attributes(response)
        .nameAttributeKey(userNameAttributeName)
        .build();
        }


    public static OAuthAttributes ofKakao(String memberNameAttributeName, Map<String, Object> attributes){
        Map<String, Object> kakaoAcount =(Map<String, Object>) attributes.get("kakaoAcount");
        Map<String, Object> profile = (Map<String, Object>) kakaoAcount.get("profile");

        log.trace("kakao acountInfo : " + kakaoAcount);

        return OAuthAttributes.builder()
                .membername((String) profile.get("Id"))
                .email((String) kakaoAcount.get("email"))
                .nickname((String) profile.get("nickname"))
                .profileImageUrl((String) profile.get("profile_image_url"))
                .attributes(kakaoAcount)
                .nameAttributeKey(memberNameAttributeName)
                .build();

    }

    public Member toEntity(){
        return Member.builder()
                .membername(membername)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .role(MemberRoleEnum.SOCIAL)
                .build();
    }
}
