package pro.tehnoplast.repository;

import org.springframework.stereotype.Repository;
import pro.tehnoplast.model.Pallet;

import java.util.List;

@Repository
public interface PalletRepository extends BaseRepository<Pallet> {

    List<Pallet> findByOrderId(Long orderId);
    
    long countByOrderId(Long orderId);
}