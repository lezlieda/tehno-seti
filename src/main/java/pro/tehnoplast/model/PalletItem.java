package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "pallet_items")
@Getter
@Setter
public class PalletItem {
    
    @EmbeddedId
    private PalletItemId id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // Связи с другими сущностями
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pallet_id", insertable = false, updatable = false)
    private Pallet pallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", insertable = false, updatable = false)
    private OrderItem orderItem;

    /**
     * Конструктор по умолчанию
     */
    public PalletItem() {
    }

    /**
     * Конструктор с основными полями
     */
    public PalletItem(Long palletId, Long orderItemId, Integer quantity) {
        this.id = new PalletItemId(palletId, orderItemId);
        this.quantity = quantity;
    }

    /**
     * Получить ID паллеты
     */
    public Long getPalletId() {
        return id != null ? id.getPalletId() : null;
    }

    /**
     * Получить ID позиции заказа
     */
    public Long getOrderItemId() {
        return id != null ? id.getOrderItemId() : null;
    }

    /**
     * Установить ID паллеты
     */
    public void setPalletId(Long palletId) {
        if (this.id == null) {
            this.id = new PalletItemId();
        }
        this.id.setPalletId(palletId);
    }

    /**
     * Установить ID позиции заказа
     */
    public void setOrderItemId(Long orderItemId) {
        if (this.id == null) {
            this.id = new PalletItemId();
        }
        this.id.setOrderItemId(orderItemId);
    }

    /**
     * Получить товар
     */
    public Product getProduct() {
        return orderItem != null ? orderItem.getProduct() : null;
    }

    /**
     * Получить название товара
     */
    public String getProductName() {
        Product product = getProduct();
        return product != null ? product.getName() : null;
    }

    /**
     * Получить внутренний штрихкод товара
     */
    public String getProductBarcode() {
        Product product = getProduct();
        return product != null ? product.getInternalBarcode() : null;
    }

    /**
     * Получить внутренний SKU товара
     */
    public String getProductSku() {
        Product product = getProduct();
        return product != null ? product.getInternalSku() : null;
    }

    /**
     * Получить группу товара
     */
    public String getProductGroup() {
        Product product = getProduct();
        return product != null ? product.getGroupName() : null;
    }

    /**
     * Получить цену за единицу товара
     */
    public BigDecimal getUnitPrice() {
        return orderItem != null ? orderItem.getUnitPrice() : BigDecimal.ZERO;
    }

    /**
     * Получить общую стоимость товара на паллете
     */
    public BigDecimal getTotalPrice() {
        if (quantity == null || getUnitPrice() == null) {
            return BigDecimal.ZERO;
        }
        return getUnitPrice().multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Получить упаковочный коэффициент товара
     */
    public Double getPackingCoefficient() {
        Product product = getProduct();
        return product != null ? product.getPackingCoefficient() : null;
    }

    /**
     * Рассчитать количество мест, занимаемых на паллете
     */
    public double calculateOccupiedPlaces() {
        Double coefficient = getPackingCoefficient();
        if (coefficient == null || quantity == null) {
            return 0.0;
        }
        return quantity * coefficient;
    }

    /**
     * Получить номер паллеты
     */
    public int getPalletNumber() {
        return pallet != null ? pallet.getPalletNumber() : 0;
    }

    /**
     * Получить номер заказа
     */
    public String getOrderNumber() {
        return pallet != null ? pallet.getOrderNumber() : null;
    }

    /**
     * Получить общее количество товара в заказе
     */
    public Integer getTotalOrderQuantity() {
        return orderItem != null ? orderItem.getQuantity() : null;
    }

    /**
     * Получить процент от общего количества в заказе
     */
    public double getPercentageOfOrder() {
        Integer totalQuantity = getTotalOrderQuantity();
        if (totalQuantity == null || totalQuantity == 0 || quantity == null) {
            return 0.0;
        }
        return (double) quantity / totalQuantity * 100;
    }

    /**
     * Проверить, помещается ли весь товар из заказа на эту паллету
     */
    public boolean isCompleteOrderItem() {
        Integer totalQuantity = getTotalOrderQuantity();
        return quantity != null && quantity.equals(totalQuantity);
    }

    /**
     * Получить остаток товара, не размещенного на паллетах
     */
    public int getRemainingQuantity() {
        if (orderItem == null) {
            return 0;
        }
        return orderItem.getRemainingQuantity();
    }


    /**
     * Проверить совместимость с другим товаром на паллете
     */
    public boolean isCompatibleWith(PalletItem other) {
        if (other == null) {
            return true;
        }
        
        String thisGroup = getProductGroup();
        String otherGroup = other.getProductGroup();
        
        // Товары одной группы совместимы
        return thisGroup != null && thisGroup.equals(otherGroup);
        
        // Можно добавить дополнительные правила совместимости
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
        return String.format("%s [%s] × %d шт. = %s руб. (%.1f%% от заказа)", 
                getProductName(), getProductSku(), quantity, getTotalPrice(), getPercentageOfOrder());
    }

    /**
     * Получить детальную информацию для упаковочного листа
     */
    public String getPackingDetails() {
        StringBuilder details = new StringBuilder();
        details.append(String.format("Товар: %s\n", getProductName()));
        details.append(String.format("Артикул: %s\n", getProductSku()));
        details.append(String.format("Штрихкод: %s\n", getProductBarcode()));
        details.append(String.format("Группа: %s\n", getProductGroup()));
        details.append(String.format("Количество: %d шт.\n", quantity));
        details.append(String.format("Цена: %s руб./шт.\n", getUnitPrice()));
        details.append(String.format("Сумма: %s руб.\n", getTotalPrice()));
        
        if (getPackingCoefficient() != null) {
            details.append(String.format("Занимает мест: %.2f\n", calculateOccupiedPlaces()));
        }
        
        if (!isCompleteOrderItem()) {
            details.append(String.format("Из заказа: %.1f%% (%d из %d шт.)\n", 
                    getPercentageOfOrder(), quantity, getTotalOrderQuantity()));
        }
        
        return details.toString();
    }

    /**
     * Получить статус размещения
     */
    public String getPlacementStatus() {
        if (isCompleteOrderItem()) {
            return "Полностью размещено";
        } else {
            return String.format("Частично размещено (%.1f%%)", getPercentageOfOrder());
        }
    }

    /**
     * Проверить валидность количества
     */
    public boolean isValidQuantity() {
        if (quantity == null || quantity <= 0) {
            return false;
        }
        
        Integer totalQuantity = getTotalOrderQuantity();
        if (totalQuantity != null && quantity > totalQuantity) {
            return false;
        }
        
        return true;
    }

    @PrePersist
    @PreUpdate
    protected void validateQuantity() {
        if (!isValidQuantity()) {
            throw new IllegalArgumentException("Некорректное количество товара на паллете");
        }
    }

    @Override
    public String toString() {
        return "PalletItem{" +
                "palletId=" + getPalletId() +
                ", orderItemId=" + getOrderItemId() +
                ", quantity=" + quantity +
                ", productName='" + getProductName() + "'" +
                ", totalPrice=" + getTotalPrice() +
                ", placementStatus='" + getPlacementStatus() + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PalletItem that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * Составной ключ для таблицы pallet_items
     */
    @Embeddable
    @Getter
    @Setter
    public static class PalletItemId {
        
        @Column(name = "pallet_id")
        private Long palletId;

        @Column(name = "order_item_id")
        private Long orderItemId;

        public PalletItemId() {
        }

        public PalletItemId(Long palletId, Long orderItemId) {
            this.palletId = palletId;
            this.orderItemId = orderItemId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PalletItemId that)) return false;
            return palletId != null && palletId.equals(that.palletId) &&
                   orderItemId != null && orderItemId.equals(that.orderItemId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(palletId, orderItemId);
        }
    }
}