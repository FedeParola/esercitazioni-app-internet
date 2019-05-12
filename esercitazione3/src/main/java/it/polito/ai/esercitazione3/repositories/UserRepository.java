package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
    public Optional<User> findByEmail(String email);
}
