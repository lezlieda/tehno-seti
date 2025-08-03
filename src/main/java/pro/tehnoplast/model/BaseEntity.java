package pro.tehnoplast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Метод для мягкого удаления сущности
     */
    public void softDelete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Метод для восстановления мягко удаленной сущности
     */
    public void restore() {
        this.isDeleted = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Проверка, удалена ли сущность
     */
    public boolean isDeleted() {
        return Boolean.TRUE.equals(isDeleted);
    }

    /**
     * Проверка, активна ли сущность (не удалена)
     */
    public boolean isActive() {
        return !isDeleted();
    }
}