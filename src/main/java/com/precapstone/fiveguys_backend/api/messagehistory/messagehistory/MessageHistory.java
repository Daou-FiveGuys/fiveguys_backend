package com.precapstone.fiveguys_backend.api.messagehistory.messagehistory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.precapstone.fiveguys_backend.api.sendimage.SendImage;
import com.precapstone.fiveguys_backend.entity.Contact2;
import com.precapstone.fiveguys_backend.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_history")
public class MessageHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageHistoryId;

    @JsonBackReference
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String fromNumber;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @JsonManagedReference
    @JoinColumn(name = "contact2_id")
    @OneToMany(fetch = FetchType.LAZY)
    private List<Contact2> contact2s;
    
    @JsonManagedReference
    @JoinColumn(name = "send_image_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SendImage sendImage;
}
