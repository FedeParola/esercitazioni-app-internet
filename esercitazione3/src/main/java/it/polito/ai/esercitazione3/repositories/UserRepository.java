package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, String> {
}
