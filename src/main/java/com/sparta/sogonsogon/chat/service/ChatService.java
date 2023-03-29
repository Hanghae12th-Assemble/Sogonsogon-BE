package com.sparta.sogonsogon.chat.service;

import com.sparta.sogonsogon.chat.dto.ChattingDto;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import com.sparta.sogonsogon.radio.entity.Radio;
import com.sparta.sogonsogon.radio.repository.RadioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RadioRepository radioRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createChat(Long radioId, ChattingDto chattingDto) {
        Radio radio = radioRepository.findById(radioId).orElseThrow(
                ()-> new EntityNotFoundException("해당 라디오를 찾을 수 없습니다. ")
        );
        Member member = memberRepository.findMemberByMembername(chattingDto.getSender()).orElseThrow(
                ()-> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다. ")
        );



    }
}
