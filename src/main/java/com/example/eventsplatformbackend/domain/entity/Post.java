package com.example.eventsplatformbackend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

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
    @NotBlank
    @Column(name = "city")
    String city;
    @Column(name = "registration_limit")
    Integer registrationLimit;
    @Column(name = "begin_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Date beginDate;
    @Column(name = "end_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Date endDate;
    @Column(name = "location")
    String location;
    @Column(name = "description")
    String description;
    @Column(name = "image")
    String image;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id")
    User owner;
}
