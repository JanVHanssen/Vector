package org.example.repository;

import org.example.model.ProductionOrder;
import org.example.model.RewindOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewindOrderRepository extends JpaRepository<RewindOrder, Long> {
    @Query("select c from RewindOrder c " +
            "where lower(cast(c.sevenNumber as string)) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(cast(c.fourNumber as string)) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(cast(c.customer as string)) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(cast(c.description as string)) like lower(concat('%', :searchTerm, '%')) ")
    List<RewindOrder> search(@Param("searchTerm") String searchTerm);
}
