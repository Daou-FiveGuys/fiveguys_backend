package com.precapstone.fiveguys_backend.api.contact2;

import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.group2.Group2Service;
import com.precapstone.fiveguys_backend.entity.Contact2;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.precapstone.fiveguys_backend.exception.errorcode.Contact2ErrorCode.CONTACT2_NAME_ALREADY_EXISTS_IN_THIS_GROUP2;
import static com.precapstone.fiveguys_backend.exception.errorcode.Contact2ErrorCode.CONTACT2_NOT_FOUND;
import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_AUTHORIZATION_FAILED;

@Service
@RequiredArgsConstructor
public class Contact2Service {
    private final Contact2Repository contact2Repository;
    private final Group2Service group2Service;
    private final JwtTokenProvider jwtTokenProvider;

    public Contact2 create(Contact2CreateDTO contact2CreateDTO, String accessToken) {
        var group2 = group2Service.readGroup2(contact2CreateDTO.getGroup2Id(), accessToken);

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!group2.getFolder2().getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        // 1. [예외처리] 본인 Group2 안에 Contact2가 이미 존재하는 경우
        var contact2s = group2.getContact2s();
        for(var contact : contact2s)
            if(contact.getName().equals(contact2CreateDTO.getName()))
                throw new ControlledException(CONTACT2_NAME_ALREADY_EXISTS_IN_THIS_GROUP2);

        var contact2 = Contact2.builder()
                .group2(group2)
                .name(contact2CreateDTO.getName())
                .telNum(contact2CreateDTO.getTelNum())
                .one(contact2CreateDTO.getOne())
                .two(contact2CreateDTO.getTwo())
                .three(contact2CreateDTO.getThree())
                .four(contact2CreateDTO.getFour())
                .five(contact2CreateDTO.getFive())
                .six(contact2CreateDTO.getSix())
                .seven(contact2CreateDTO.getSeven())
                .eight(contact2CreateDTO.getEight())
                .build();

        contact2Repository.save(contact2);
        return contact2;
    }

    public Contact2 readContact2(Long contact2Id, String accessToken) {
        var contact2 = contact2Repository.findById(contact2Id)
                .orElseThrow(()-> new ControlledException(CONTACT2_NOT_FOUND));

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!contact2.getGroup2().getFolder2().getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        return contact2;
    }

    public Contact2 update(Contact2UpdateDTO contact2UpdateDTO, String accessToken) {
        var contact2 = readContact2(contact2UpdateDTO.getContact2Id(), accessToken);

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        // ※ 이미 readContact2()에서 인증을 거치지만 형식상 추가
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!contact2.getGroup2().getFolder2().getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        if(contact2UpdateDTO.getName() != null)
            contact2.setName(contact2UpdateDTO.getName());

        if(contact2UpdateDTO.getTelNum() != null)
            contact2.setTelNum(contact2UpdateDTO.getTelNum());

        if(contact2UpdateDTO.getOne() != null) contact2.setOne(contact2UpdateDTO.getOne());
        if(contact2UpdateDTO.getTwo() != null) contact2.setTwo(contact2UpdateDTO.getTwo());
        if(contact2UpdateDTO.getThree() != null) contact2.setThree(contact2UpdateDTO.getThree());
        if(contact2UpdateDTO.getFour() != null) contact2.setFour(contact2UpdateDTO.getFour());
        if(contact2UpdateDTO.getFive() != null) contact2.setFive(contact2UpdateDTO.getFive());
        if(contact2UpdateDTO.getSix() != null) contact2.setSix(contact2UpdateDTO.getSix());
        if(contact2UpdateDTO.getSeven() != null) contact2.setSeven(contact2UpdateDTO.getSeven());
        if(contact2UpdateDTO.getEight() != null) contact2.setEight(contact2UpdateDTO.getEight());

        contact2Repository.save(contact2);
        return contact2;
    }

    public Contact2 delete(Long contact2Id, String accessToken) {
        var contact2 = readContact2(contact2Id, accessToken);

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        // ※ 이미 readContact2()에서 인증을 거치지만 형식상 추가
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!contact2.getGroup2().getFolder2().getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        contact2Repository.deleteById(contact2Id);
        return contact2;
    }
}
