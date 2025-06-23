package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code_gln;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String region;

    @Column(name = "short_name", nullable = false)
    private String shortName;
}
