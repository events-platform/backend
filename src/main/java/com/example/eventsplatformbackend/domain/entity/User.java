package com.example.eventsplatformbackend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Username is mandatory")
    @Column(name = "username", unique = true)
    String username;
    @Column(name = "about")
    String about;
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Email does not match regex")
    @NotBlank
    @Column(name = "email", unique = true)
    String email;
    @Column(name = "phone", unique = true)
    @Pattern(regexp="^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", message = "Phone does not match regex")
    String phone;
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password cannot be shorter of 8 characters")
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
    @ManyToMany
    @JoinTable(
            name = "users_subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    Set<Post> subscribedPosts;
    @Column(name = "favorite_posts")
    @ManyToMany
    @JoinTable(
            name = "users_favorite_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    Set<Post> favoritePosts;
}
