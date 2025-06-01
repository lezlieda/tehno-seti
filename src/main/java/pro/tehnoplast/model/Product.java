package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, length = 13)
    private String barcode; // EAN13

    @Column(nullable = false)
    private Double coefficient;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    public enum ProductType {
        PLASTIC, METAL, PND
    }
}
