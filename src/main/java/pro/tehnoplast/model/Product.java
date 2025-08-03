package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "internal_barcode", nullable = false, unique = true, length = 50)
    private String internalBarcode;

    @Column(name = "external_barcode", length = 50)
    private String externalBarcode;

    @Column(name = "internal_sku", nullable = false, unique = true, length = 50)
    private String internalSku;

    @Column(name = "external_sku", length = 50)
    private String externalSku;

    @Column(name = "packing_coefficient", nullable = false)
    private Double packingCoefficient;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private ProductGroup productGroup;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    /**
     * Конструктор по умолчанию
     */
    public Product() {
    }

    /**
     * Конструктор с основными полями
     */
    public Product(String name, String internalBarcode, String internalSku, 
                   Double packingCoefficient, Long groupId) {
        this.name = name;
        this.internalBarcode = internalBarcode;
        this.internalSku = internalSku;
        this.packingCoefficient = packingCoefficient;
        this.groupId = groupId;
    }

    /**
     * Получить основной штрихкод (внутренний или внешний)
     */
    public String getPrimaryBarcode() {
        return internalBarcode != null ? internalBarcode : externalBarcode;
    }

    /**
     * Получить основной SKU (внутренний или внешний)
     */
    public String getPrimarySku() {
        return internalSku != null ? internalSku : externalSku;
    }

    /**
     * Проверить, есть ли внешние коды
     */
    public boolean hasExternalCodes() {
        return externalBarcode != null || externalSku != null;
    }

    /**
     * Получить название группы товара
     */
    public String getGroupName() {
        return productGroup != null ? productGroup.getDisplayName() : null;
    }

    /**
     * Рассчитать количество мест на паллете для заданного количества
     */
    public int calculatePalletPlaces(int quantity) {
        if (packingCoefficient == null || packingCoefficient <= 0) {
            return 0;
        }
        return (int) Math.ceil(quantity / packingCoefficient);
    }

    /**
     * Проверить валидность упаковочного коэффициента
     */
    public boolean isValidPackingCoefficient() {
        return packingCoefficient != null && packingCoefficient > 0;
    }

    /**
     * Получить краткое название (первые 50 символов)
     */
    public String getShortName() {
        if (name == null) return null;
        return name.length() > 50 ? name.substring(0, 47) + "..." : name;
    }

    /**
     * Получить отображаемое название для UI
     */
    public String getDisplayName() {
        return String.format("%s [%s]", getShortName(), getPrimarySku());
    }


    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", internalSku='" + internalSku + "'" +
                ", packingCoefficient=" + packingCoefficient +
                ", groupName='" + getGroupName() + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}