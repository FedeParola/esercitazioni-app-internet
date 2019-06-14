package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.Pupil;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PupilRepository extends CrudRepository<Pupil, Long> {
}
