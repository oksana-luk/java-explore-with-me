package ru.practicum.ewm;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HitDto {
    private long id;

    @NotBlank(message = "App should not be empty")
    private String app;

    @NotBlank(message = "Uri should not be empty")
    private String uri;

    @NotBlank(message = "Ip should not be empty")
    private String ip;

    @NotNull(message = "Timestamp should not be empty")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
