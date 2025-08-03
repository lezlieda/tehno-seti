package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "pallets")
@Getter
@Setter
public class Pallet extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // Связи с другими сущностями
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;

    @OneToMany(mappedBy = "pallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PalletItem> palletItems;

    /**
     * Конструктор по умолчанию
     */
    public Pallet() {
    }

    /**
     * Конструктор с заказом
     */
    public Pallet(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * Получить номер заказа
     */
    public String getOrderNumber() {
        return order != null ? order.getNumber() : null;
    }

    /**
     * Получить дату доставки заказа
     */
    public java.time.LocalDate getDeliveryDate() {
        return order != null ? order.getDeliveryDate() : null;
    }

    /**
     * Получить номер паллеты в рамках заказа
     */
    public int getPalletNumber() {
        if (order == null || order.getPallets() == null) {
            return 1;
        }
        
        List<Pallet> orderPallets = order.getPallets();
        for (int i = 0; i < orderPallets.size(); i++) {
            if (orderPallets.get(i).getId().equals(this.id)) {
                return i + 1;
            }
        }
        return 1;
    }

    /**
     * Получить количество позиций на паллете
     */
    public int getItemsCount() {
        return palletItems != null ? palletItems.size() : 0;
    }

    /**
     * Получить общее количество товаров на паллете
     */
    public int getTotalQuantity() {
        if (palletItems == null || palletItems.isEmpty()) {
            return 0;
        }
        return palletItems.stream()
                .mapToInt(PalletItem::getQuantity)
                .sum();
    }

    /**
     * Получить общую стоимость товаров на паллете
     */
    public BigDecimal getTotalValue() {
        if (palletItems == null || palletItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return palletItems.stream()
                .map(PalletItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Получить общий вес паллеты (если есть данные о весе товаров)
     */
    public double getTotalWeight() {
        if (palletItems == null || palletItems.isEmpty()) {
            return 0.0;
        }
        return palletItems.stream()
                .mapToDouble(item -> {
                    Product product = item.getProduct();
                    if (product != null && product.getPackingCoefficient() != null) {
                        return product.getPackingCoefficient() * item.getQuantity();
                    }
                    return 0.0;
                })
                .sum();
    }

    /**
     * Получить процент заполнения паллеты
     */
    public double getFillPercentage() {
        if (palletItems == null || palletItems.isEmpty()) {
            return 0.0;
        }
        
        double totalPlaces = palletItems.stream()
                .mapToDouble(item -> {
                    Product product = item.getProduct();
                    if (product != null && product.getPackingCoefficient() != null) {
                        return item.getQuantity() * product.getPackingCoefficient();
                    }
                    return 0.0;
                })
                .sum();
        
        // Предполагаем, что паллета вмещает 100 условных мест
        return Math.min(totalPlaces, 100.0);
    }

    /**
     * Проверить, пустая ли паллета
     */
    public boolean isEmpty() {
        return palletItems == null || palletItems.isEmpty();
    }

    /**
     * Проверить, полная ли паллета
     */
    public boolean isFull() {
        return getFillPercentage() >= 100.0;
    }

    /**
     * Проверить, можно ли добавить товар на паллету
     */
    public boolean canAddItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }
        
        if (product.getPackingCoefficient() == null) {
            return true; // Если нет коэффициента, считаем что можно добавить
        }
        
        double additionalPlaces = quantity * product.getPackingCoefficient();
        return (getFillPercentage() + additionalPlaces) <= 100.0;
    }

    /**
     * Получить список групп товаров на паллете
     */
    public List<String> getProductGroups() {
        if (palletItems == null || palletItems.isEmpty()) {
            return List.of();
        }
        
        return palletItems.stream()
                .map(item -> item.getProduct())
                .filter(product -> product != null)
                .map(Product::getGroupName)
                .filter(group -> group != null)
                .distinct()
                .toList();
    }

    /**
     * Проверить, содержит ли паллета товары разных групп
     */
    public boolean hasMixedGroups() {
        return getProductGroups().size() > 1;
    }

    /**
     * Получить основную группу товаров на паллете
     */
    public String getPrimaryGroup() {
        List<String> groups = getProductGroups();
        return groups.isEmpty() ? null : groups.get(0);
    }

    /**
     * Получить отображаемое название для UI
     */
    public String getDisplayName() {
        return String.format("Паллета №%d (Заказ %s)", getPalletNumber(), getOrderNumber());
    }

    /**
     * Получить краткую информацию о паллете
     */
    public String getSummary() {
        return String.format("Паллета №%d: %d поз., %d шт., %.1f%% заполнения", 
                getPalletNumber(), getItemsCount(), getTotalQuantity(), getFillPercentage());
    }

    /**
     * Получить статус паллеты
     */
    public String getStatus() {
        if (isEmpty()) {
            return "Пустая";
        } else if (isFull()) {
            return "Полная";
        } else if (getFillPercentage() >= 80.0) {
            return "Почти полная";
        } else if (getFillPercentage() >= 50.0) {
            return "Частично заполнена";
        } else {
            return "Мало заполнена";
        }
    }

    /**
     * Получить детальную информацию для упаковочного листа
     */
    public String getPackingDetails() {
        StringBuilder details = new StringBuilder();
        details.append(String.format("Паллета №%d\n", getPalletNumber()));
        details.append(String.format("Заказ: %s\n", getOrderNumber()));
        details.append(String.format("Позиций: %d\n", getItemsCount()));
        details.append(String.format("Общее количество: %d шт.\n", getTotalQuantity()));
        details.append(String.format("Общая стоимость: %s руб.\n", getTotalValue()));
        details.append(String.format("Заполнение: %.1f%%\n", getFillPercentage()));
        
        if (hasMixedGroups()) {
            details.append("⚠️ Смешанные группы товаров: ").append(String.join(", ", getProductGroups()));
        } else {
            details.append("Группа товаров: ").append(getPrimaryGroup());
        }
        
        return details.toString();
    }

    /**
     * Добавить товар на паллету (бизнес-метод)
     */
    public boolean addItem(OrderItem orderItem, int quantity) {
        if (orderItem == null || quantity <= 0) {
            return false;
        }
        
        if (!canAddItem(orderItem.getProduct(), quantity)) {
            return false;
        }
        
        // Проверяем, есть ли уже такой товар на паллете
        if (palletItems != null) {
            for (PalletItem existingItem : palletItems) {
                if (existingItem.getOrderItemId().equals(orderItem.getId())) {
                    // Товар уже есть, увеличиваем количество
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    return true;
                }
            }
        }
        
        // Создаем новую позицию на паллете
        PalletItem newItem = new PalletItem(this.id, orderItem.getId(), quantity);
        if (palletItems == null) {
            palletItems = new java.util.ArrayList<>();
        }
        palletItems.add(newItem);
        
        return true;
    }

    @Override
    public String toString() {
        return "Pallet{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", palletNumber=" + getPalletNumber() +
                ", itemsCount=" + getItemsCount() +
                ", totalQuantity=" + getTotalQuantity() +
                ", fillPercentage=" + String.format("%.1f%%", getFillPercentage()) +
                ", status='" + getStatus() + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pallet pallet)) return false;
        return id != null && id.equals(pallet.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}