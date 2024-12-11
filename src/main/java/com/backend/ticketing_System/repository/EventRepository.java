package com.backend.ticketing_System.repository;

import com.backend.ticketing_System.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Event entities.
 */
public interface EventRepository extends MongoRepository<Event, String> {
    /**
     * Finds all events by the owner's ID.
     *
     * @param ownerId the ID of the owner.
     * @return a list of events associated with the given owner ID.
     */
    List<Event> findAllByOwnerId(String ownerId);

    /**
     * Finds an event by its name.
     *
     * @param name the name of the event.
     * @return an Optional containing the found event, or empty if no event was found.
     */
    Optional<Event> findByName(String name);
}