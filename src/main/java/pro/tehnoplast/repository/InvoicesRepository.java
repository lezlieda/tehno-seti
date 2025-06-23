package pro.tehnoplast.repository;

import pro.tehnoplast.model.Invoice;

import java.util.Optional;

public interface InvoicesRepository extends BaseRepository<Invoice>{
    Optional<Invoice> findByNumber(String invoiceNumber);
    Optional<Invoice> findByOrderId(Long orderId);
}
