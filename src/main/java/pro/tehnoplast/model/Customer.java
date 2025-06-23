package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tax_id", nullable = false, unique = true)
    private String taxId;

    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(name = "contact_info", nullable = false)
    private String contactInfo;
}
