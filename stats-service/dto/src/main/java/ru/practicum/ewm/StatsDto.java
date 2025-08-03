package ru.practicum.ewm;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class StatsDto {
    private String app;
    private String uri;
    private long hits;

    public StatsDto(String app, String uri, long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatsDto statsDto)) return false;

        return app.equals(statsDto.app) && uri.equals(statsDto.uri);
    }

    @Override
    public int hashCode() {
        int result = app.hashCode();
        result = 31 * result + uri.hashCode();
        return result;
    }
}
