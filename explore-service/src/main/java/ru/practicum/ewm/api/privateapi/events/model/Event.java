package ru.practicum.ewm.api.privateapi.events.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.api.adminapi.categories.model.Category;
import ru.practicum.ewm.api.adminapi.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    private String title;

    private String description;

    private String annotation;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Embedded
    private Location location = new Location();

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    private boolean paid;

    @Enumerated(value = EnumType.STRING)
    private State state;

    public enum State {
        PENDING, PUBLISHED, CANCELED;
    }
}
