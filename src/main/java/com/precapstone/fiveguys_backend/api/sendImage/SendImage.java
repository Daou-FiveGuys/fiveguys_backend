package com.precapstone.fiveguys_backend.api.sendImage;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.precapstone.fiveguys_backend.api.messagehistory.MessageHistory;
import com.precapstone.fiveguys_backend.entity.Image;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "send_image_id")
public class SendImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sendImageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_history_id")
    @JsonBackReference
    private MessageHistory messageHistory;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    @JsonBackReference
    private Image image;

    @Column(nullable = false)
    private String imageLink;

    @Enumerated(EnumType.STRING)
    private DeleteYn deleteYn;
}
