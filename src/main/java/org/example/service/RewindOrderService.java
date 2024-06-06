package org.example.service;

import org.example.model.ProductionOrder;
import org.example.model.RewindOrder;
import org.example.repository.ProductionOrderRepository;
import org.example.repository.RewindOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RewindOrderService {
    private final RewindOrderRepository rewindOrderRepository;


    public RewindOrderService(RewindOrderRepository rewindOrderRepository) {
        this.rewindOrderRepository = rewindOrderRepository;

    }
    public RewindOrder createRewindOrder(RewindOrder rewindOrder) {
        return rewindOrderRepository.save(rewindOrder);
    }
    public List<RewindOrder> findAllRewindOrders() {
        return rewindOrderRepository.findAll();
    }

    public Optional<RewindOrder> getRewindOrderById(Long id) {
        return rewindOrderRepository.findById(id);
    }

    public void deleteRewindOrder(RewindOrder rewindOrder) {
        rewindOrderRepository.delete(rewindOrder);
    }

    public void saveRewindOrder(RewindOrder rewindOrder) {
        if (rewindOrder == null) {
            System.err.println("RewindOrder is null. Are you sure you have connected your form to the application?");
            return;
        }
        rewindOrderRepository.save(rewindOrder);
    }
    public List<RewindOrder> findAllRewindOrders(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return rewindOrderRepository.findAll();
        } else {
            return rewindOrderRepository.search(stringFilter);
        }
    }
}

