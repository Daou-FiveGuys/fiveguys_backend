package com.precapstone.fiveguys_backend.api.reservation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.precapstone.fiveguys_backend.entity.messagehistory.MessageHistory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @JsonBackReference
    @JoinColumn(name = "message_history_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private MessageHistory messageHistory;

    @Column(nullable = false)
    private LocalDateTime sendTime;

    @Builder.Default
    @Column(nullable = false)
    private ReservationState state = ReservationState.NOTYET;
}
