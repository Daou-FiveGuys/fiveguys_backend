package com.precapstone.fiveguys_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.precapstone.fiveguys_backend.entity.messagehistory.MessageHistory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "send_image")
public class SendImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sendImageId;

    @JsonBackReference
    @JoinColumn(name = "message_history_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MessageHistory messageHistory;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;
}
