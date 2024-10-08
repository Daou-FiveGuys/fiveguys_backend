package com.precapstone.fiveguys_backend.member;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.ResponseMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {
    private MemberRepository memberRepository;
    public MemberService(MemberRepository memberRepository) { this.memberRepository = memberRepository; }
    public MemberRepository getMemberRepository() { return memberRepository; }

    public CommonResponse findMemberByUsername(String username) throws EntityNotFoundException {
        Optional<Member> member = Optional.ofNullable(memberRepository
                .findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Member not found")));

        return CommonResponse.builder()
                .code(200)
                .message(ResponseMessage.SUCCESS)
                .data(member)
                .build();
    }
}
