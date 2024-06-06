package org.example.repository;

import org.example.model.OldDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OldDescriptionRepository extends JpaRepository<OldDescription, Long> {
}
