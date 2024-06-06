package org.example.service;

import org.example.model.OldDescription;
import org.example.repository.OldDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OldDescriptionService {

    private final OldDescriptionRepository oldDescriptionRepository;

    @Autowired
    public OldDescriptionService(OldDescriptionRepository oldDescriptionRepository) {
        this.oldDescriptionRepository = oldDescriptionRepository;
    }

    public OldDescription saveOldDescription(OldDescription oldDescription) {
        return oldDescriptionRepository.save(oldDescription);
    }

    public List<OldDescription> findAllOldDescriptions() {
        return oldDescriptionRepository.findAll();
    }

    public Optional<OldDescription> getOldDescriptionById(Long id) {
        return oldDescriptionRepository.findById(id);
    }

    public void deleteOldDescription(OldDescription oldDescription) {
        oldDescriptionRepository.delete(oldDescription);
    }
}