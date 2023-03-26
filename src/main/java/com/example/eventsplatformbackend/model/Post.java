package com.example.eventsplatformbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    Long id;
    @Column(name = "name")
    @NotBlank
    String name;
    @Column(name = "description")
    String description;
    @Column(name = "date")
    @NotBlank
    Date date;
    @Column(name = "image")
    String imgSource;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "owner_user_id")
    User owner;
}
