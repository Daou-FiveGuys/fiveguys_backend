package com.precapstone.fiveguys_backend.api.reservation;

import com.precapstone.fiveguys_backend.entity.messagehistory.MessageHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Reservation create(MessageHistory messageHistory, LocalDateTime sendTime) {
        var reservation = Reservation.builder()
                .messageHistory(messageHistory)
                .sendTime(sendTime)
                .build();

        reservationRepository.save(reservation);
        return reservation;
    }

    public List<Reservation> readAll(List<MessageHistory> messageHistories) {

        // 3. MessageHistory에서 Reservation 리스트 생성
        List<Reservation> reservations = messageHistories.stream()
                .map(MessageHistory::getReservation) // MessageHistory에서 Reservation 추출
                .filter(Objects::nonNull)            // Reservation이 null이 아닌 경우만 포함
                .toList();

        for (var reservation : reservations)
            if (reservation.getSendTime().isBefore(LocalDateTime.now(ZoneId.of("Asia/Seoul"))) && reservation.getState() == ReservationState.NOTYET)
                changeType(reservation.getMessageHistory(), ReservationState.DONE);

        return reservations;
    }

    public void changeType(MessageHistory messageHistory, ReservationState reservationState) {
        var reservation = messageHistory.getReservation();
        reservation.setState(reservationState);
        reservationRepository.save(reservation);
    }
}
