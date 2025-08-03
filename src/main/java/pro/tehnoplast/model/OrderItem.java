package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 12, scale = 2, insertable = false, updatable = false)
    private BigDecimal totalPrice;

    // Связи с другими сущностями
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PalletItem> palletItems;

    /**
     * Конструктор по умолчанию
     */
    public OrderItem() {
    }

    /**
     * Конструктор с основными полями
     */
    public OrderItem(Long orderId, Long productId, Integer quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Получить название товара
     */
    public String getProductName() {
        return product != null ? product.getName() : null;
    }

    /**
     * Получить внутренний штрихкод товара
     */
    public String getProductBarcode() {
        return product != null ? product.getInternalBarcode() : null;
    }

    /**
     * Получить внутренний SKU товара
     */
    public String getProductSku() {
        return product != null ? product.getInternalSku() : null;
    }

    /**
     * Получить группу товара
     */
    public String getProductGroup() {
        return product != null ? product.getGroupName() : null;
    }

    /**
     * Получить номер заказа
     */
    public String getOrderNumber() {
        return order != null ? order.getNumber() : null;
    }

    /**
     * Получить упаковочный коэффициент товара
     */
    public Double getPackingCoefficient() {
        return product != null ? product.getPackingCoefficient() : null;
    }

    /**
     * Рассчитать количество мест на паллете для данной позиции
     */
    public int calculatePalletPlaces() {
        if (product == null || !product.isValidPackingCoefficient()) {
            return 0;
        }
        return product.calculatePalletPlaces(quantity);
    }

    /**
     * Получить общее количество товара, размещенного на паллетах
     */
    public int getQuantityOnPallets() {
        if (palletItems == null || palletItems.isEmpty()) {
            return 0;
        }
        return palletItems.stream()
                .mapToInt(PalletItem::getQuantity)
                .sum();
    }

    /**
     * Получить остаток товара, не размещенного на паллетах
     */
    public int getRemainingQuantity() {
        return quantity - getQuantityOnPallets();
    }

    /**
     * Проверить, полностью ли размещена позиция на паллетах
     */
    public boolean isFullyPacked() {
        return getRemainingQuantity() == 0;
    }

    /**
     * Проверить, есть ли товар на паллетах
     */
    public boolean hasItemsOnPallets() {
        return getQuantityOnPallets() > 0;
    }

    /**
     * Получить процент размещения на паллетах
     */
    public double getPackingPercentage() {
        if (quantity == 0) return 0.0;
        return (double) getQuantityOnPallets() / quantity * 100;
    }

    /**
     * Рассчитать общую стоимость (для случаев, когда поле не вычисляется БД)
     */
    public BigDecimal calculateTotalPrice() {
        if (quantity == null || unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Получить итоговую стоимость (из БД или рассчитанную)
     */
    public BigDecimal getTotalPrice() {
        return totalPrice != null ? totalPrice : calculateTotalPrice();
    }

    /**
     * Получить отображаемое название для UI
     */
    public String getDisplayName() {
        return String.format("%s × %d шт.", getProductName(), quantity);
    }

    /**
     * Получить краткую информацию о позиции
     */
    public String getSummary() {
        return String.format("%s [%s] × %d шт. = %s руб.", 
                getProductName(), getProductSku(), quantity, getTotalPrice());
    }

    /**
     * Получить статус размещения
     */
    public String getPackingStatus() {
        if (!hasItemsOnPallets()) {
            return "Не размещено";
        } else if (isFullyPacked()) {
            return "Полностью размещено";
        } else {
            return String.format("Частично размещено (%.1f%%)", getPackingPercentage());
        }
    }


    protected void validateQuantity() {
        if (quantity != null && quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть больше 0");
        }
        if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + getTotalPrice() +
                ", packingStatus='" + getPackingStatus() + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}