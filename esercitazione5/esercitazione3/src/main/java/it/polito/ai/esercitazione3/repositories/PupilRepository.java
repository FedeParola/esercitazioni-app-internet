package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.Pupil;
import org.springframework.data.repository.CrudRepository;

public interface PupilRepository extends CrudRepository<Pupil, Long> {
}
