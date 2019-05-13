package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.RecoverToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecoverTokenRepository extends CrudRepository<RecoverToken, Long> {
    Optional<RecoverToken> findByUuid(String uuid);
}
