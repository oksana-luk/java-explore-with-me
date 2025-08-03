package ru.practicum.ewm;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hits")
public class HitDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "App should not be empty")
    @Column(name = "app")
    private String app;

    @NotBlank(message = "Uri should not be empty")
    @Column(name = "uri")
    private String uri;

    @NotBlank(message = "Ip should not be empty")
    @Column(name = "ip")
    private String ip;

    @NotNull(message = "Timestamp should not be empty")
    @Column(name = "timestamp_value")
    private LocalDateTime timestamp;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HitDto hitDto)) return false;

        return id == hitDto.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
