package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.Line;
import org.springframework.data.repository.CrudRepository;

public interface LineRepository extends CrudRepository<Line, Long> {
    public Line getByName(String name);
}
