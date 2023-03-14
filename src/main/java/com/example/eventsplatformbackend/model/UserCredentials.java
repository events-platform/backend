package com.example.eventsplatformbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Entity
@Table(name = "user_credentials")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCredentials {
    @Id
    @Column(name = "user_id")
    Long id;
    @NotNull
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Malformed email")
    String email;
    @NotNull
    @Size(min = 6, message = "Password cannot be shorter, than 6 characters")
    String password;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    User user;
}
