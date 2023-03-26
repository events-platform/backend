package com.example.eventsplatformbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    Long id;
    @NotBlank(message = "username is mandatory")
    @Column(name = "username")
    String username;
    @Column(name = "first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Email does not match regex")
    @Column(name = "email")
    String email;
    @NotBlank(message = "password is mandatory")
    @Size(min = 6, message = "Password cannot be shorter, than 6 characters")
    @Column(name = "password")
    String password;
    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    Role role = Role.USER;
}
