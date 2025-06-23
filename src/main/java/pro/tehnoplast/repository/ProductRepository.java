package pro.tehnoplast.repository;

import org.springframework.stereotype.Repository;
import pro.tehnoplast.model.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product>{
    Optional<Product> findByBarcode(String barcode);
    Optional<Product> findByName(String name);
}
