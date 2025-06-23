package pro.tehnoplast.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pro.tehnoplast.model.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends BaseRepository<Order> {
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT * FROM proto.orders o WHERE o.delivery_date = :deliveryDate")
    List<Order> findOrdersByDeliveryDate(@Param("deliveryDate") LocalDate deliveryDate);

    @Query("SELECT * FROM proto.orders o WHERE o.delivery_date = :deliveryDate AND o.region = :region")
    List<Order> findOrdersByDeliveryDateAndRegion(@Param("deliveryDate") LocalDate deliveryDate, @Param("region") String region);
}
