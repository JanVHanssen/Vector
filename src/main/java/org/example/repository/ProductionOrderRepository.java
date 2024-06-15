package org.example.repository;

import org.example.model.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
    @Query("select c from ProductionOrder c " +
            "where lower(cast(c.sevenNumber as string)) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(cast(c.fourNumber as string)) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(cast(c.customer.name as string)) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(cast(c.description.name as string)) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(cast(c.ml as string)) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(cast(c.sequence as string)) like lower(concat('%', :searchTerm, '%'))")
    List<ProductionOrder> search(@Param("searchTerm") String searchTerm);
}
