package com.example.eventsplatformbackend.domain.entity;

import com.example.eventsplatformbackend.domain.enumeration.EFormat;
import com.example.eventsplatformbackend.domain.enumeration.EType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    Long id;
    @NotBlank
    @Column(name = "name")
    String name;
    @NotNull
    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    EFormat format;
    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    EType type;
    @Column(name = "registration_limit")
    Integer registrationLimit;
    @Column(name = "begin_date")
    @Temporal(TemporalType.TIMESTAMP)
    LocalDateTime beginDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    LocalDateTime endDate;
    @Column(name = "location")
    String location;
    @Column(name = "description")
    String description;
    @Column(name = "email")
    String email;
    @Column(name = "link")
    String externalLink;
    @Column(name = "image")
    String image;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id")
    User owner;
}
