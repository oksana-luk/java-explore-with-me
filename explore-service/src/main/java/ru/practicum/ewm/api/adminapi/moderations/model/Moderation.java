package ru.practicum.ewm.api.adminapi.moderations.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.api.adminapi.user.model.User;
import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "event_moderations")
public class Moderation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @Enumerated(EnumType.STRING)
    private Event.State state;

    @Column(name = "moderated_on", nullable = false, updatable = false)
    private LocalDateTime moderateOn = LocalDateTime.now();

    private String comment;
}
