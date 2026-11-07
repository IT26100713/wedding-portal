package com.WD08.WeddingMangementSystem.event.controller;

import com.WD08.WeddingMangementSystem.event.dto.EventForm;
import com.WD08.WeddingMangementSystem.event.model.Event;
import com.WD08.WeddingMangementSystem.event.service.EventService;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EventControllerTest {

    private EventService eventService;
    private EventController controller;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        controller = new EventController(eventService);
    }

    @Test
    void dashboardShowsEventSummary() {
        when(eventService.countEvents()).thenReturn(3L);
        when(eventService.countEventStatus("UPCOMING")).thenReturn(2L);
        when(eventService.countEventStatus("COMPLETED")).thenReturn(1L);
        when(eventService.countEventStatus("CANCELLED")).thenReturn(0L);
        when(eventService.getRecentEvents(5)).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        assertThat(controller.dashboard(model)).isEqualTo("event/dashboard");
        assertThat(model.get("totalEvents")).isEqualTo(3L);
        assertThat(model.get("upcomingEvents")).isEqualTo(2L);
    }

    @Test
    void listPreservesSearchAndStatusFilters() {
        when(eventService.findEvents("ceremony", "UPCOMING")).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        assertThat(controller.listEvents("ceremony", "UPCOMING", model)).isEqualTo("event/events");
        assertThat(model.get("search")).isEqualTo("ceremony");
        assertThat(model.get("status")).isEqualTo("UPCOMING");
        verify(eventService).findEvents("ceremony", "UPCOMING");
    }

    @Test
    void invalidCreateReturnsFormWithoutSaving() {
        EventForm form = new EventForm();
        BindingResult errors = new BeanPropertyBindingResult(form, "eventForm");
        errors.rejectValue("eventName", "NotBlank");
        ExtendedModelMap model = new ExtendedModelMap();

        assertThat(controller.createEvent(form, errors, new RedirectAttributesModelMap(), model))
                .isEqualTo("event/form");
        assertThat(model.get("pageTitle")).isEqualTo("Create Event");
        verify(eventService, never()).createEvent(form);
    }

    @Test
    void emptyEventFormFailsRequiredFieldValidation() {
        var factory = Validation.buildDefaultValidatorFactory();
        try {
            assertThat(factory.getValidator().validate(new EventForm()))
                    .extracting(violation -> violation.getPropertyPath().toString())
                    .contains("eventName", "eventDate", "startTime", "venue",
                            "guestCount", "estimatedBudget", "description");
        } finally {
            factory.close();
        }
    }

    @Test
    void createAndUpdateRedirectAfterSaving() {
        EventForm form = new EventForm();
        Event saved = event("EV001");
        when(eventService.createEvent(form)).thenReturn(saved);
        RedirectAttributesModelMap createRedirect = new RedirectAttributesModelMap();

        assertThat(controller.createEvent(form, new BeanPropertyBindingResult(form, "eventForm"),
                createRedirect, new ExtendedModelMap())).isEqualTo("redirect:/events");
        assertThat(createRedirect.getFlashAttributes().get("successMessage").toString()).contains("EV001");

        when(eventService.updateEvent("EV001", form)).thenReturn(saved);
        RedirectAttributesModelMap updateRedirect = new RedirectAttributesModelMap();
        assertThat(controller.updateEvent("EV001", form, new BeanPropertyBindingResult(form, "eventForm"),
                updateRedirect, new ExtendedModelMap())).isEqualTo("redirect:/events");
        assertThat(updateRedirect.getFlashAttributes().get("successMessage").toString()).contains("updated");
    }

    @Test
    void viewEditAndDeleteUseEventId() {
        Event event = event("EV002");
        when(eventService.getEventById("EV002")).thenReturn(Optional.of(event));
        ExtendedModelMap detailsModel = new ExtendedModelMap();
        assertThat(controller.getEvent("EV002", detailsModel)).isEqualTo("event/details");
        assertThat(detailsModel.get("event")).isSameAs(event);

        ExtendedModelMap editModel = new ExtendedModelMap();
        assertThat(controller.showEditEventForm("EV002", editModel)).isEqualTo("event/form");
        assertThat(editModel.get("eventId")).isEqualTo("EV002");

        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        assertThat(controller.deleteEvent("EV002", redirect)).isEqualTo("redirect:/events");
        verify(eventService).deleteEvent("EV002");
    }

    @Test
    void eventTemplatesAndAssetsArePackagedAtTheirExpectedPaths() {
        assertThat(new ClassPathResource("templates/event/dashboard.html").exists()).isTrue();
        assertThat(new ClassPathResource("templates/event/events.html").exists()).isTrue();
        assertThat(new ClassPathResource("templates/event/form.html").exists()).isTrue();
        assertThat(new ClassPathResource("templates/event/details.html").exists()).isTrue();
        assertThat(new ClassPathResource("templates/event/error.html").exists()).isTrue();
        assertThat(new ClassPathResource("templates/event/fragments/event-nav.html").exists()).isTrue();
        assertThat(new ClassPathResource("static/event/css/event-style.css").exists()).isTrue();
        assertThat(new ClassPathResource("static/event/js/event.js").exists()).isTrue();
    }

    private Event event(String eventId) {
        Event event = new Event();
        event.setEventId(eventId);
        event.setEventName("Wedding");
        return event;
    }
}
