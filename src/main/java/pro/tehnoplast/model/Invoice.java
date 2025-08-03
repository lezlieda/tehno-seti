package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, length = 50)
    private String number;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "order_id", unique = true)
    private Long orderId;

    @Column(name = "counteragent_inn", length = 12)
    private String counteragentInn;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counteragent_inn", insertable = false, updatable = false)
    private Counteragent counteragent;

    /**
     * Конструктор по умолчанию
     */
    public Invoice() {
    }

    /**
     * Конструктор с основными полями
     */
    public Invoice(String number, LocalDate issueDate, Long orderId, String counteragentInn) {
        this.number = number;
        this.issueDate = issueDate;
        this.orderId = orderId;
        this.counteragentInn = counteragentInn;
    }

    /**
     * Получить номер заказа
     */
    public String getOrderNumber() {
        return order != null ? order.getNumber() : null;
    }

    /**
     * Получить название контрагента
     */
    public String getCounteragentName() {
        return counteragent != null ? counteragent.getName() : null;
    }

    /**
     * Получить дату заказа
     */
    public LocalDate getOrderDate() {
        return order != null ? order.getOrderDate() : null;
    }

    /**
     * Получить дату доставки заказа
     */
    public LocalDate getDeliveryDate() {
        return order != null ? order.getDeliveryDate() : null;
    }

    /**
     * Проверить, выставлен ли счет в срок (до даты доставки)
     */
    public boolean isIssuedOnTime() {
        if (issueDate == null || getDeliveryDate() == null) {
            return false;
        }
        return !issueDate.isAfter(getDeliveryDate());
    }

    /**
     * Получить количество дней между выставлением счета и доставкой
     */
    public long getDaysToDelivery() {
        if (issueDate == null || getDeliveryDate() == null) {
            return 0;
        }
        return getDeliveryDate().toEpochDay() - issueDate.toEpochDay();
    }

    /**
     * Получить отображаемое название для UI
     */
    public String getDisplayName() {
        return String.format("Счет №%s от %s", number, issueDate);
    }

    /**
     * Получить краткую информацию о счете
     */
    public String getSummary() {
        return String.format("Счет №%s от %s (Заказ: %s, Контрагент: %s)", 
                number, issueDate, getOrderNumber(), getCounteragentName());
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", number='" + number + "'" +
                ", issueDate=" + issueDate +
                ", orderId=" + orderId +
                ", counteragentInn='" + counteragentInn + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}