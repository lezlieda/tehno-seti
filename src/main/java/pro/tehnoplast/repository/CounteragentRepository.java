package pro.tehnoplast.repository;

import org.springframework.stereotype.Repository;
import pro.tehnoplast.model.Counteragent;

import java.util.Optional;

@Repository
public interface CounteragentRepository extends BaseRepository<Counteragent> {
    Optional<Counteragent> findByInn(String inn);
    Optional<Counteragent> findByName(String name);
}
