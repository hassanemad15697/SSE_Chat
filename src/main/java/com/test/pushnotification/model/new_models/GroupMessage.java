package com.test.pushnotification.model.new_models;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "GroupMessages")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer groupMessageId;

    @ManyToOne
    @JoinColumn(name = "GroupID", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "SenderUserID", nullable = false)
    private User sender;

    @Column(nullable = false)
    private String messageText;

    @Column(nullable = false)
    private LocalDateTime sentAt;
}
