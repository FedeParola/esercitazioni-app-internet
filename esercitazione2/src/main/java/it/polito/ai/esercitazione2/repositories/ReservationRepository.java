package it.polito.ai.esercitazione2.repositories;

import it.polito.ai.esercitazione2.entities.Reservation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
}
