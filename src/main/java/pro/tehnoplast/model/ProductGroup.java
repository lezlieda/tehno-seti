package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "product_groups")
@Getter
@Setter
public class ProductGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private ProductGroupType name;

    @OneToMany(mappedBy = "productGroup", fetch = FetchType.LAZY)
    private List<Product> products;

    /**
     * Enum для типов групп товаров согласно ограничению в БД
     */
    public enum ProductGroupType {
        пластик("пластик"),
        металл("металл"),
        ПНД("ПНД");

        private final String displayName;

        ProductGroupType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Конструктор по умолчанию
     */
    public ProductGroup() {
    }

    /**
     * Конструктор с типом группы
     */
    public ProductGroup(ProductGroupType name) {
        this.name = name;
    }

    /**
     * Получить отображаемое название группы
     */
    public String getDisplayName() {
        return name != null ? name.getDisplayName() : null;
    }

    @Override
    public String toString() {
        return "ProductGroup{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductGroup)) return false;
        ProductGroup that = (ProductGroup) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}