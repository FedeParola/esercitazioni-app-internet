package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.ConfirmationToken;
import it.polito.ai.esercitazione3.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {
    public Optional<ConfirmationToken> findByUser(User user);
    public Optional<ConfirmationToken> findByUuid(String uuid);
}
