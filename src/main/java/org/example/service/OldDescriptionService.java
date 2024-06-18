package org.example.service;

import org.example.model.Description;
import org.example.model.OldDescription;
import org.example.repository.OldDescriptionRepository;
import org.example.repository.ProductionOrderRepository;
import org.example.repository.RewindOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OldDescriptionService {

    private final OldDescriptionRepository oldDescriptionRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final RewindOrderRepository rewindOrderRepository;

    @Autowired
    public OldDescriptionService(OldDescriptionRepository oldDescriptionRepository,
                                 ProductionOrderRepository productionOrderRepository,
                                 RewindOrderRepository rewindOrderRepository) {
        this.oldDescriptionRepository = oldDescriptionRepository;
        this.productionOrderRepository = productionOrderRepository;
        this.rewindOrderRepository = rewindOrderRepository;
    }

    public OldDescription saveOldDescription(OldDescription oldDescription) {
        Optional<OldDescription> existingOldDescription = oldDescriptionRepository.findByName(oldDescription.getName());
        if (existingOldDescription.isPresent() && !existingOldDescription.get().getId().equals(oldDescription.getId())) {
            throw new IllegalArgumentException("Old Name already exists");
        }
        return oldDescriptionRepository.save(oldDescription);
    }

    public List<OldDescription> findAllOldDescriptions() {
        return oldDescriptionRepository.findAll();
    }

    public Optional<OldDescription> getOldDescriptionById(Long id) {
        return oldDescriptionRepository.findById(id);
    }

    public void deleteOldDescription(OldDescription oldDescription) {
        if (productionOrderRepository.existsByOldDescription(oldDescription) || rewindOrderRepository.existsByOldDescription(oldDescription)) {
            throw new IllegalStateException("Cannot delete old description with existing orders");
        }
        oldDescriptionRepository.delete(oldDescription);
    }
}