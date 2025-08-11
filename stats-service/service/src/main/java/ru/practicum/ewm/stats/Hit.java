package ru.practicum.ewm.stats;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "app")
    private String app;

    @Column(name = "uri")
    private String uri;

    @Column(name = "ip")
    private String ip;

    @Column(name = "timestamp_value")
    private LocalDateTime timestamp;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hit hit)) return false;

        return id == hit.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}

