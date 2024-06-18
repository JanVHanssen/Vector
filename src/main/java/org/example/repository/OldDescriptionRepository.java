package org.example.repository;

import org.example.model.Description;
import org.example.model.OldDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OldDescriptionRepository extends JpaRepository<OldDescription, Long> {
    Optional<OldDescription> findByName(String name);
}
