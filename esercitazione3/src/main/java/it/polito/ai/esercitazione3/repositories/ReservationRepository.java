package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.Reservation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
}
