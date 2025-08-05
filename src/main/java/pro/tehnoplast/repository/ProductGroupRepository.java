package pro.tehnoplast.repository;

import org.springframework.stereotype.Repository;
import pro.tehnoplast.model.ProductGroup;

import java.util.Optional;

@Repository
public interface ProductGroupRepository extends BaseRepository<ProductGroup> {
    Optional<ProductGroup> findByName(String name);
}