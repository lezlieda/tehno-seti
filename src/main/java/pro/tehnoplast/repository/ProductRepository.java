package pro.tehnoplast.repository;

import org.springframework.stereotype.Repository;
import pro.tehnoplast.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product> {
    Optional<Product> findByInternalBarcode(String internalBarcode);
    Optional<Product> findByInternalSku(String internalSku);
    Optional<Product> findByExternalBarcode(String externalBarcode);
    Optional<Product> findByExternalSku(String externalSku);
    Optional<Product> findByName(String name);
    List<Product> findAllByCategory(String category);
}