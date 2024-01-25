package com.test.pushnotification.model.new_models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserMessages")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name = "SenderUserID", nullable = false)
    private com.test.pushnotification.model.new_models.User sender;

    @ManyToOne
    @JoinColumn(name = "RecipientUserID", nullable = false)
    private User recipient;

    @Column(nullable = false)
    private String messageText;

    @Column(nullable = false)
    private LocalDateTime sentAt;
}


