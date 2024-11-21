package com.precapstone.fiveguys_backend.api.sendImage;

import com.precapstone.fiveguys_backend.entity.Image;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_history")
public class SendImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageHistoryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(nullable = false)
    private String imageLink;

    @Enumerated(EnumType.STRING)
    private DeleteYn deleteYn;
}
