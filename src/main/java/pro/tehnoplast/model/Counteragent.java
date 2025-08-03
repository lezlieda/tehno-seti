package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "counteragents")
@Getter
@Setter
public class Counteragent extends BaseEntity {
    
    @Id
    @Column(name = "inn", length = 12)
    private String inn;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @OneToMany(mappedBy = "counteragent", fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "counteragent", fetch = FetchType.LAZY)
    private List<Invoice> invoices;

    /**
     * Конструктор по умолчанию
     */
    public Counteragent() {
    }

    /**
     * Конструктор с ИНН и названием
     */
    public Counteragent(String inn, String name) {
        this.inn = inn;
        this.name = name;
    }

    /**
     * Проверка валидности ИНН (базовая проверка длины)
     */
    public boolean isValidInn() {
        return inn != null && (inn.length() == 10 || inn.length() == 12) && inn.matches("\\d+");
    }

    /**
     * Получить тип контрагента по длине ИНН
     */
    public String getCounteragentType() {
        if (inn == null) return "Неизвестно";
        return switch (inn.length()) {
            case 10 -> "Юридическое лицо";
            case 12 -> "Индивидуальный предприниматель";
            default -> "Некорректный ИНН";
        };
    }

    /**
     * Получить краткое название (первые 50 символов)
     */
    public String getShortName() {
        if (name == null) return null;
        return name.length() > 50 ? name.substring(0, 47) + "..." : name;
    }

    @Override
    public String toString() {
        return "Counteragent{" +
                "inn='" + inn + "'" +
                ", name='" + name + "'" +
                ", type='" + getCounteragentType() + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Counteragent that)) return false;
        return inn != null && inn.equals(that.inn);
    }

    @Override
    public int hashCode() {
        return inn != null ? inn.hashCode() : 0;
    }
}