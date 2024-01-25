package com.test.pushnotification.model.new_models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "userid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "passwordhash", nullable = false)
    private String passwordHash;

    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Column(name = "gender", nullable = true)
    private String gender;

    @Column(name = "dateofbirth")
    private LocalDate dateOfBirth;

    @Column(nullable = false, name = "createdat")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updatedat")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

