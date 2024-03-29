package com.sparta.sogonsogon.member.entity;

import com.sparta.sogonsogon.follow.entity.Follow;
import com.sparta.sogonsogon.member.dto.MemberRequestDto;
import com.sparta.sogonsogon.member.dto.SignUpRequestDto;
import com.sparta.sogonsogon.noti.entity.Notification;
import com.sparta.sogonsogon.radio.entity.Radio;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.*;

@Entity(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email // @가 없거나 영문이 아닌 한글인 경우, 특수기호는 오류
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String membername;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(nullable = true)
    private String memberInfo;

    @Column(unique = true)
    private Long kakaoId;

    @Column(unique = true)
    private String naverId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberRoleEnum role ;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "radio_Id")
    private List<Radio> radios = new ArrayList<>();

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "radio_id")
//    private Radio radio;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notification = new ArrayList<>();

    @OneToMany(mappedBy = "following")
    private List<Follow> follower = new ArrayList<>();

    @OneToMany(mappedBy = "follower")
    private List<Follow> following = new ArrayList<>();

    public Member(SignUpRequestDto requestDto, String password){
        this.membername = requestDto.getMembername();
        this.nickname = requestDto.getNickname();
        this.email = requestDto.getEmail();
        this.password = password;
        this.role = MemberRoleEnum.USER;
    }

    public void update(MemberRequestDto requestDto){
        this.nickname = requestDto.getNickname();
        this.memberInfo = requestDto.getMemberInfo();
        this.profileImageUrl = requestDto.getProfileImageUrl();
    }

    //네이버 회원 정보
    @Builder
    public Member(String naverId, String email, String profileImageUrl, String password, String nickname, String membername){
        this.membername = membername;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.password = password;
        this.nickname = nickname;
        this.naverId = naverId;
        this.role = MemberRoleEnum.SOCIAL;
    }

    //카카오 회원 정보
    public Member(String nickname, String profileImageUrl, String password, String email, Long kakaoId, String membername){
        this.membername = membername;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.nickname = nickname;
        this.kakaoId = kakaoId;
        this.role = MemberRoleEnum.SOCIAL;

    }



}
