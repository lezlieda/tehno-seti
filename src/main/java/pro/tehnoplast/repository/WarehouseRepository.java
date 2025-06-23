package pro.tehnoplast.repository;

import org.springframework.stereotype.Repository;
import pro.tehnoplast.model.Warehouse;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends BaseRepository<Warehouse>{
    Optional<Warehouse> findByGln(String gln);
}
