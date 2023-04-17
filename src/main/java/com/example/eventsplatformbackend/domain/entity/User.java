package com.example.eventsplatformbackend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    Long id;
    @NotBlank(message = "username is mandatory")
    @Column(name = "username")
    String username;
    @Column(name = "about")
    String about;
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "email does not match regex")
    @Column(name = "email")
    String email;
    @Column(name = "phone")
    @Pattern(regexp="^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$")
    String phone;
    @NotBlank(message = "password is mandatory")
    @Size(min = 8, message = "password cannot be shorter, than 8 characters")
    @Column(name = "password")
    String password;
    @Column(name = "avatar")
    @ColumnDefault("'src/main/resources/templates/avatar.jpg'")
    String avatar;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    ERole role = ERole.ROLE_USER;
    @Column(name = "created_posts")
    @OneToMany
    Set<Post> createdPosts;
    @Column(name = "subscribed_posts")
    @OneToMany
    @JoinTable(
            name = "users_subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    Set<Post> subscribedPosts;
    @Column(name = "favorite_posts")
    @OneToMany
    @JoinTable(
            name = "users_favorite_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    Set<Post> favoritePosts;
}
