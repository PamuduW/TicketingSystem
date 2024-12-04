package com.backend.ticketing_System.repository;

import com.backend.ticketing_System.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findAllByOwnerId(String ownerId);

    Optional<Event> findByName(String name);
}