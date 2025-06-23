package pro.tehnoplast.repository;

import org.springframework.stereotype.Repository;
import pro.tehnoplast.model.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends BaseRepository<Customer> {
    Optional<Customer> findByTaxId(String taxId);
}
