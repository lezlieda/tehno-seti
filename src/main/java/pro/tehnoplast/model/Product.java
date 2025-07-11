package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String barCode;

    @Column(nullable = false)
    private Double coefficient;

    @Column(nullable = false)
    private ProductType type;

    public enum ProductType {
        PLASTIC, METAL, PND
    }
}
