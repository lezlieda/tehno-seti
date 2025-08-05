package pro.tehnoplast.repository;

import pro.tehnoplast.model.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoicesRepository extends BaseRepository<Invoice>{
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByIssueDate(String issueDate);
    Optional<Invoice> findByOrderId(Long orderId);
}
