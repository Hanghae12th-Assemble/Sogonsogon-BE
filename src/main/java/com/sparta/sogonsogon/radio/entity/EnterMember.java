package com.sparta.sogonsogon.radio.entity;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.TimeStamped;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EnterMember extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Member member;

    @ManyToOne
    @JoinColumn(name = "radio_id")
    private Radio radio;

    public EnterMember(Member member, Radio radio) {
        this.member = member;
        this.radio = radio;
    }
}
