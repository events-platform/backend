package com.example.eventsplatformbackend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

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
    @Column(name = "first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "email does not match regex")
    @Column(name = "email")
    String email;
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
}
