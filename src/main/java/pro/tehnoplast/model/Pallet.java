package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Pallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer palletNumber;
    private Integer totalPallets;
    private Integer positionsCount;
    private LocalDate shippingDate;

    @OneToMany(mappedBy = "pallet", cascade = CascadeType.ALL)
    private List<PalletItem> items;
}
