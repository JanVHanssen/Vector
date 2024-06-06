package org.example.service;

import org.example.model.Description;
import org.example.repository.DescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DescriptionService {

    private final DescriptionRepository descriptionRepository;

    @Autowired
    public DescriptionService(DescriptionRepository descriptionRepository) {
        this.descriptionRepository = descriptionRepository;
    }

    public Description saveDescription(Description description) {
        return descriptionRepository.save(description);
    }

    public List<Description> findAllDescriptions() {
        return descriptionRepository.findAll();
    }

    public Optional<Description> getDescriptionById(Long id) {
        return descriptionRepository.findById(id);
    }

    public void deleteDescription(Description description) {
        descriptionRepository.delete(description);
    }
}
