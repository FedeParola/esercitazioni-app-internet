package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
