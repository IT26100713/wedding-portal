package com.oop.wedding.service;

import com.oop.wedding.dto.EventForm;
import com.oop.wedding.entity.Event;
import com.oop.wedding.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.persistence.criteria.Predicate;

@Service
public class EventService {

    private static final Object EVENT_ID_LOCK = new Object();
    private static final Set<String> VALID_STATUSES = Set.of("UPCOMING", "COMPLETED", "CANCELLED");
    private static final Pattern EVENT_ID_PATTERN = Pattern.compile("EV(\\d+)");

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findEvents(String search, String status) {
        Specification<Event> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(search)) {
                String searchTerm = "%" + search.trim() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("eventId")), searchTerm.toLowerCase(Locale.ROOT)),
                        cb.like(cb.lower(root.get("eventName")), searchTerm.toLowerCase(Locale.ROOT))
                ));
            }

            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(cb.lower(root.get("status")), status.trim().toLowerCase(Locale.ROOT)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return eventRepository.findAll(specification);
    }

    public List<Event> getRecentEvents(int limit) {
        return eventRepository.findTop10ByOrderByEventDateDesc().stream()
                .limit(limit)
                .toList();
    }

    public long countEvents() {
        return eventRepository.count();
    }

    public long countEventStatus(String status) {
        String normalized = normalizeStatus(status);
        return eventRepository.findAll((root, query, cb) ->
                        cb.equal(cb.lower(root.get("status")), normalized.toLowerCase(Locale.ROOT)))
                .size();
    }

    public Optional<Event> getEventById(String eventId) {
        return eventRepository.findByEventId(eventId);
    }

    @Transactional
    public Event createEvent(EventForm form) {
        Event event = new Event();
        applyFormData(event, form);
        event.setEventId(generateNextEventId());
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(String eventId, EventForm form) {
        Event existingEvent = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));

        applyFormData(existingEvent, form);
        return eventRepository.save(existingEvent);
    }

    @Transactional
    public void deleteEvent(String eventId) {
        Event existingEvent = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));
        eventRepository.delete(existingEvent);
    }

    public String generateNextEventId() {
        synchronized (EVENT_ID_LOCK) {
            Optional<Event> latestEvent = eventRepository.findTopByOrderByEventIdDesc();
            int nextNumber = 1;

            if (latestEvent.isPresent() && latestEvent.get().getEventId() != null) {
                Matcher matcher = EVENT_ID_PATTERN.matcher(latestEvent.get().getEventId().trim());
                if (matcher.matches()) {
                    nextNumber = Integer.parseInt(matcher.group(1)) + 1;
                }
            }

            return String.format("EV%03d", nextNumber);
        }
    }

    private void applyFormData(Event event, EventForm form) {
        event.setEventName(StringUtils.hasText(form.getEventName()) ? form.getEventName().trim() : null);
        event.setEventDate(form.getEventDate());
        event.setStartTime(form.getStartTime());
        event.setVenue(StringUtils.hasText(form.getVenue()) ? form.getVenue().trim() : null);
        event.setGuestCount(form.getGuestCount());
        event.setEstimatedBudget(form.getEstimatedBudget() == null ? BigDecimal.ZERO : form.getEstimatedBudget());
        event.setStatus(normalizeStatus(form.getStatus()));
        event.setEventType("WEDDING");
        event.setDescription(StringUtils.hasText(form.getDescription()) ? form.getDescription().trim() : null);
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "UPCOMING";
        }

        String normalizedStatus = status.trim().toUpperCase(Locale.ROOT);
        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new IllegalArgumentException("Unsupported status: " + status);
        }
        return normalizedStatus;
    }
}
