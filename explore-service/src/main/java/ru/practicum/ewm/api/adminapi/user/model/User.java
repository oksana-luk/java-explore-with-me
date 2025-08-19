package ru.practicum.ewm.api.adminapi.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        return (id != 0L)
                ? Long.hashCode(id)
                : Objects.hash(email, name, role);
    }

    public enum Role {
        ADMIN, USER;
    }
}
