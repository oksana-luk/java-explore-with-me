package ru.practicum.ewm.api.adminapi.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.api.privateapi.events.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UpdateEventAdminRequest {
    @Positive
    private Long category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Size(min = 3, max = 120, message = "Title should contain from 3 to 120 characters")
    private String title;

    @Size(min = 20, max = 7000, message = "Description should contain from 20 to 7000 characters")
    private String description;

    @Size(min = 20, max = 2000, message = "Annotation should contain from 20 to 2000 characters")
    private String annotation;

    private Boolean requestModeration;

    @Positive(message = "Participant limit should be positive")
    private Integer participantLimit;

    private Location location;

    private Boolean paid;

    private UpdateEventAdminRequest.StateAction stateAction;

    @Size(min = 5, max = 2000, message = "Comment should contain from 5 to 2000 characters")
    private String comment;

    public enum StateAction {
        PUBLISH_EVENT, FIX_EVENT, REJECT_EVENT
    }

    public boolean hasCategory() {
        return category != null;
    }

    public boolean hasTitle() {
        return ! (title == null || title.isBlank());
    }

    public boolean hasEventDate() {
        return eventDate != null;
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasAnnotation() {
        return ! (annotation == null || annotation.isBlank());
    }

    public boolean hasRequestModeration() {
        return requestModeration != null;
    }

    public boolean hasParticipantLimit() {
        return participantLimit != null;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public boolean hasPaid() {
        return paid != null;
    }

    public boolean hasStateAction() {
        return stateAction != null;
    }

    public boolean hasComment() {
        return ! (comment == null || comment.isBlank());
    }
}
