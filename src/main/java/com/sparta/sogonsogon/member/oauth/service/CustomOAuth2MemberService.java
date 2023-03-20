package com.sparta.sogonsogon.member.oauth.service;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.oauth.entity.OAuthAttributes;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2MemberService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //OAuth2 서비스 id 구분 코드
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        //OAuth2 로그인 진행시 키가 되는 필드 값
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        /* OAuth2UserService */
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = saveOrUpdate(attributes);

        //세션 정보를 직렬화하는 dto 클래스
        httpSession.setAttribute(attributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoleValue())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );

    }

    /* 소셜로그인시 기존 회원이 존재하면 수정날짜 정보만 업데이트해 기존의 데이터는 그대로 보존 */
    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                    .map(Member::updateModifiedAt)
                    .orElse(attributes.toEntity());

                    return memberRepository.save(member);
    }

}
