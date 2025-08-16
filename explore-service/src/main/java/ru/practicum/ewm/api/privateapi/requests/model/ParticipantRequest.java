package ru.practicum.ewm.api.privateapi.requests.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.api.adminapi.user.model.User;
import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class ParticipantRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    private LocalDateTime created;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING, CONFIRMED, CANCELED;
    }
}
