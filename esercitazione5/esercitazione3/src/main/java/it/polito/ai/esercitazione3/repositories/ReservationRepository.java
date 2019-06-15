package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.Pupil;
import it.polito.ai.esercitazione3.entities.Reservation;
import it.polito.ai.esercitazione3.entities.Stop;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> getByStopAndDate(Stop stop, Date date);
    List<Reservation> getByPupilAndDate(Pupil pupil, Date date);
}
