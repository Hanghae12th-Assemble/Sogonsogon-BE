package com.sparta.sogonsogon.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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


}
