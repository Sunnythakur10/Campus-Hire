package com.campushire.campus_hire.user.internal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false , length = 100)
    private String name;

    @Column(nullable = false , unique = true)
    private String email;

    @Column(name = "password_hash" , nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 20)
    private Role role;

    @Column(name = "is_Active" , nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at" , nullable = false , updatable = false)
    private LocalDateTime createdAt;



    //Automatically set the timestamp for created_at before saving to the DB
    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
