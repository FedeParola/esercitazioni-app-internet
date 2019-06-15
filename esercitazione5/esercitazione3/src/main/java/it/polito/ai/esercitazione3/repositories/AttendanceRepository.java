package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.Attendance;
import it.polito.ai.esercitazione3.entities.Pupil;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.Optional;

public interface AttendanceRepository extends CrudRepository<Attendance, Long> {
    Optional<Attendance> getByPupilAndDateAndDirection(Pupil pupil, Date date, Character direction);
}
