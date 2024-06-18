package org.example.service;

import org.example.model.Description;
import org.example.repository.DescriptionRepository;
import org.example.repository.ProductionOrderRepository;
import org.example.repository.RewindOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DescriptionService {

    private final DescriptionRepository descriptionRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final RewindOrderRepository rewindOrderRepository;

    @Autowired
    public DescriptionService(DescriptionRepository descriptionRepository,
                              ProductionOrderRepository productionOrderRepository,
                              RewindOrderRepository rewindOrderRepository) {
        this.descriptionRepository = descriptionRepository;
        this.productionOrderRepository = productionOrderRepository;
        this.rewindOrderRepository = rewindOrderRepository;
    }

    public Description saveDescription(Description description) {
        Optional<Description> existingDescription = descriptionRepository.findByName(description.getName());
        if (existingDescription.isPresent() && !existingDescription.get().getId().equals(description.getId())) {
            throw new IllegalArgumentException("Description already exists");
        }
        return descriptionRepository.save(description);
    }

    public List<Description> findAllDescriptions() {
        return descriptionRepository.findAll();
    }

    public Optional<Description> getDescriptionById(Long id) {
        return descriptionRepository.findById(id);
    }

    public void deleteDescription(Description description) {
        if (productionOrderRepository.existsByDescription(description) || rewindOrderRepository.existsByDescription(description)) {
            throw new IllegalStateException("Cannot delete description with existing orders");
        }
        descriptionRepository.delete(description);
    }
}
