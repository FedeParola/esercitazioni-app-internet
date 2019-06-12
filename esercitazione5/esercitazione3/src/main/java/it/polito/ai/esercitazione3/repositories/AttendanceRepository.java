package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.Attendance;
import org.springframework.data.repository.CrudRepository;

public interface AttendanceRepository extends CrudRepository<Attendance, Long> {
}
