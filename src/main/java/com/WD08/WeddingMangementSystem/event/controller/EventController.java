package com.WD08.WeddingMangementSystem.event.controller;

import com.WD08.WeddingMangementSystem.event.dto.EventForm;
import com.WD08.WeddingMangementSystem.event.model.Event;
import com.WD08.WeddingMangementSystem.event.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalEvents", eventService.countEvents());
        model.addAttribute("upcomingEvents", eventService.countEventStatus("UPCOMING"));
        model.addAttribute("completedEvents", eventService.countEventStatus("COMPLETED"));
        model.addAttribute("cancelledEvents", eventService.countEventStatus("CANCELLED"));
        model.addAttribute("recentEvents", eventService.getRecentEvents(5));
        return "event-dashboard";
    }

    @GetMapping
    public String listEvents(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            Model model) {

        List<Event> events = eventService.findEvents(search, status);
        model.addAttribute("events", events);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "event-list";
    }

    @GetMapping("/new")
    public String showNewEventForm(Model model) {
        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("pageTitle", "Create Event");
        return "event-form";
    }

    @PostMapping
    public String createEvent(
            @Valid @ModelAttribute("eventForm") EventForm eventForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Create Event");
            return "event-form";
        }

        Event savedEvent = eventService.createEvent(eventForm);
        redirectAttributes.addFlashAttribute("successMessage", "Event " + savedEvent.getEventId() + " was created successfully.");
        return "redirect:/events";
    }

    @GetMapping("/{eventId}")
    public String getEvent(@PathVariable String eventId, Model model) {
        Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + eventId));
        model.addAttribute("event", event);
        return "event-details";
    }

    @GetMapping("/{eventId}/edit")
    public String showEditEventForm(@PathVariable String eventId, Model model) {
        Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + eventId));

        model.addAttribute("eventForm", EventForm.fromEntity(event));
        model.addAttribute("eventId", eventId);
        model.addAttribute("pageTitle", "Edit Event");
        return "event-form";
    }

    @PostMapping("/{eventId}/edit")
    public String updateEvent(
            @PathVariable String eventId,
            @Valid @ModelAttribute("eventForm") EventForm eventForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Event");
            model.addAttribute("eventId", eventId);
            return "event-form";
        }

        Event updatedEvent = eventService.updateEvent(eventId, eventForm);
        redirectAttributes.addFlashAttribute("successMessage", "Event " + updatedEvent.getEventId() + " was updated successfully.");
        return "redirect:/events";
    }

    @PostMapping("/{eventId}/delete")
    public String deleteEvent(@PathVariable String eventId, RedirectAttributes redirectAttributes) {
        eventService.deleteEvent(eventId);
        redirectAttributes.addFlashAttribute("successMessage", "Event " + eventId + " was deleted successfully.");
        return "redirect:/events";
    }
}
