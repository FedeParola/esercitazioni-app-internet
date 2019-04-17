package it.polito.ai.esercitazione2.repositories;

import it.polito.ai.esercitazione2.entities.Line;
import org.springframework.data.repository.CrudRepository;

public interface LineRepository extends CrudRepository<Line, Long> {
    public Line getByName(String name);
}
