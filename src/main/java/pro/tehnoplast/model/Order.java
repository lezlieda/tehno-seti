package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, length = 50)
    private String number;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(name = "counteragent_inn", length = 12)
    private String counteragentInn;

    @Column(name = "warehouse_gln", length = 13)
    private String warehouseGln;

    @Column(name = "pallet_count", nullable = false)
    private Integer palletCount = 1;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // Связи с другими сущностями
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counteragent_inn", referencedColumnName = "inn", insertable = false, updatable = false)
    private Counteragent counteragent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_gln", referencedColumnName = "gln", insertable = false, updatable = false)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pallet> pallets;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Invoice invoice;

    /**
     * Конструктор по умолчанию
     */
    public Order() {
    }

    /**
     * Конструктор с основными полями
     */
    public Order(String number, LocalDate orderDate, LocalDate deliveryDate, 
                 String counteragentInn, String warehouseGln) {
        this.number = number;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.counteragentInn = counteragentInn;
        this.warehouseGln = warehouseGln;
        this.palletCount = 1;
    }

    /**
     * Получить название контрагента
     */
    public String getCounteragentName() {
        return counteragent != null ? counteragent.getName() : null;
    }

    /**
     * Получить адрес склада
     */
    public String getWarehouseAddress() {
        return warehouse != null ? warehouse.getAddress() : null;
    }

    /**
     * Получить регион склада
     */
    public String getWarehouseRegion() {
        return warehouse != null ? warehouse.getRegion() : null;
    }

    /**
     * Получить номер счета
     */
    public String getInvoiceNumber() {
        return invoice != null ? invoice.getNumber() : null;
    }

    /**
     * Проверить, есть ли счет к заказу
     */
    public boolean hasInvoice() {
        return invoice != null;
    }

    /**
     * Рассчитать общую сумму заказа
     */
    public BigDecimal getTotalAmount() {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Получить количество позиций в заказе
     */
    public int getItemsCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    /**
     * Получить общее количество товаров
     */
    public int getTotalQuantity() {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0;
        }
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * Проверить, просрочен ли заказ
     */
    public boolean isOverdue() {
        return deliveryDate != null && deliveryDate.isBefore(LocalDate.now());
    }

    /**
     * Получить количество дней до доставки
     */
    public long getDaysToDelivery() {
        if (deliveryDate == null) return 0;
        return deliveryDate.toEpochDay() - LocalDate.now().toEpochDay();
    }

    /**
     * Получить статус заказа
     */
    public String getStatus() {
        if (isOverdue()) {
            return "Просрочен";
        } else if (hasInvoice()) {
            return "Выставлен счет";
        } else if (getDaysToDelivery() <= 3) {
            return "Срочный";
        } else {
            return "В работе";
        }
    }

    /**
     * Получить отображаемое название для UI
     */
    public String getDisplayName() {
        return String.format("Заказ №%s от %s", number, orderDate);
    }

    /**
     * Получить краткую информацию о заказе
     */
    public String getSummary() {
        return String.format("Заказ №%s от %s (Доставка: %s, Контрагент: %s)", 
                number, orderDate, deliveryDate, getCounteragentName());
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (palletCount == null) {
            palletCount = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", number='" + number + "'" +
                ", orderDate=" + orderDate +
                ", deliveryDate=" + deliveryDate +
                ", palletCount=" + palletCount +
                ", status='" + getStatus() + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}