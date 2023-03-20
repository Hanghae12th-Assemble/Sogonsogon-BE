package com.sparta.sogonsogon.member.entity;

import com.sparta.sogonsogon.follow.entity.Follow;
import com.sparta.sogonsogon.member.dto.MemberRequestDto;
import com.sparta.sogonsogon.member.dto.SignUpRequestDto;
import com.sparta.sogonsogon.noti.entity.Notification;
import com.sparta.sogonsogon.radio.entity.Radio;
import lombok.*;

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

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberRoleEnum role = MemberRoleEnum.USER;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "radio_Id")
    private List<Radio> radio = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_Id")
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
    }

    public void update(MemberRequestDto requestDto){
        this.nickname = requestDto.getNickname();
        this.memberInfo = requestDto.getMemberInfo();
        this.profileImageUrl = requestDto.getProfileImageUrl();
    }

//    public Member update(String nickname, String profileImageUrl) {
//        this.nickname = nickname;
//        this.profileImageUrl = profileImageUrl;
//        return this;
//    }


}
