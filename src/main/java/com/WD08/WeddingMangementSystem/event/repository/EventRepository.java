package com.WD08.WeddingMangementSystem.event.repository;

import com.WD08.WeddingMangementSystem.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Optional<Event> findByEventId(String eventId);

    Optional<Event> findTopByOrderByEventIdDesc();

    List<Event> findTop10ByOrderByEventDateDesc();
}
