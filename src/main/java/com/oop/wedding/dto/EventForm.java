package com.oop.wedding.dto;

import com.oop.wedding.entity.Event;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventForm {

    @NotBlank(message = "Event name is required")
    private String eventName;

    @NotNull(message = "Event date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate eventDate;

    @NotNull(message = "Start time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotNull(message = "Guest count is required")
    @Min(value = 1, message = "Guest count must be at least 1")
    private Integer guestCount;

    @NotNull(message = "Estimated budget is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Estimated budget cannot be negative")
    private BigDecimal estimatedBudget;

    @NotBlank(message = "Status is required")
    private String status = "UPCOMING";

    @NotBlank(message = "Description is required")
    private String description;

    public EventForm() {
    }

    public EventForm(String eventName, LocalDate eventDate, LocalTime startTime,
                    String venue, Integer guestCount, BigDecimal estimatedBudget,
                    String status, String description) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.venue = venue;
        this.guestCount = guestCount;
        this.estimatedBudget = estimatedBudget;
        this.status = status;
        this.description = description;
    }

    public static EventForm fromEntity(Event event) {
        return new EventForm(
                event.getEventName(),
                event.getEventDate(),
                event.getStartTime(),
                event.getVenue(),
                event.getGuestCount(),
                event.getEstimatedBudget(),
                event.getStatus(),
                event.getDescription()
        );
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public BigDecimal getEstimatedBudget() {
        return estimatedBudget;
    }

    public void setEstimatedBudget(BigDecimal estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
