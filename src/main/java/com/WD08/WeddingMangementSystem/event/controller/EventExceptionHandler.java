package com.WD08.WeddingMangementSystem.event.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice(assignableTypes = EventController.class)
public class EventExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatus(ResponseStatusException exception, Model model) {
        model.addAttribute("errorCode", exception.getStatusCode().value());
        model.addAttribute("errorMessage", exception.getReason());
        return "event/error";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(EntityNotFoundException exception, Model model) {
        model.addAttribute("errorCode", 404);
        model.addAttribute("errorMessage", exception.getMessage());
        return "event/error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException exception, Model model) {
        model.addAttribute("errorCode", 400);
        model.addAttribute("errorMessage", exception.getMessage());
        return "event/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception exception, Model model) {
        model.addAttribute("errorCode", 500);
        model.addAttribute("errorMessage",
                exception.getMessage() != null ? exception.getMessage() : "Unexpected internal server error.");
        return "event/error";
    }
}
