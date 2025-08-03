package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
public class Warehouse extends BaseEntity {
    
    @Id
    @Column(name = "gln", length = 13)
    private String gln;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "region", nullable = false, length = 100)
    private String region;

    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    private List<Order> orders;

    /**
     * Конструктор по умолчанию
     */
    public Warehouse() {
    }

    /**
     * Конструктор с GLN, адресом и регионом
     */
    public Warehouse(String gln, String address, String region) {
        this.gln = gln;
        this.address = address;
        this.region = region;
    }

    /**
     * Проверка валидности GLN (должен быть 13 цифр)
     */
    public boolean isValidGln() {
        return gln != null && gln.length() == 13 && gln.matches("\\d+");
    }

    /**
     * Получить краткий адрес (первые 100 символов)
     */
    public String getShortAddress() {
        if (address == null) return null;
        return address.length() > 100 ? address.substring(0, 97) + "..." : address;
    }

    /**
     * Получить полное название склада (регион + краткий адрес)
     */
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (region != null) {
            sb.append(region);
        }
        if (address != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(getShortAddress());
        }
        return sb.toString();
    }

    /**
     * Получить отображаемое название для UI
     */
    public String getDisplayName() {
        return String.format("%s (%s)", getFullName(), gln);
    }

    /**
     * Проверить, находится ли склад в указанном регионе
     */
    public boolean isInRegion(String regionName) {
        return region != null && region.toLowerCase().contains(regionName.toLowerCase());
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "gln='" + gln + "'" +
                ", region='" + region + "'" +
                ", address='" + getShortAddress() + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Warehouse that)) return false;
        return gln != null && gln.equals(that.gln);
    }

    @Override
    public int hashCode() {
        return gln != null ? gln.hashCode() : 0;
    }
}