package org.example.service;

import org.example.model.ProductionOrder;
import org.example.model.RewindOrder;
import org.example.repository.RewindOrderRepository;
import org.springframework.stereotype.Service;
import org.example.repository.ProductionOrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductionOrderService {
    private final ProductionOrderRepository productionOrderRepository;


    public ProductionOrderService(ProductionOrderRepository productionOrderRepository) {
        this.productionOrderRepository = productionOrderRepository;

    }
    public ProductionOrder createProductionOrder(ProductionOrder productionOrder) {
        return productionOrderRepository.save(productionOrder);
    }
    public List<ProductionOrder> findAllProductionOrders() {
        return productionOrderRepository.findAll();
    }

    public Optional<ProductionOrder> getProductionOrderById(Long id) {
        return productionOrderRepository.findById(id);
    }

    public void deleteProductionOrder(ProductionOrder productionOrder) {
        productionOrderRepository.delete(productionOrder);
    }

    public void saveProductionOrder(ProductionOrder productionOrder) {
        if (productionOrder == null) {
            System.err.println("ProductionOrder is null. Are you sure you have connected your form to the application?");
            return;
        }
        productionOrderRepository.save(productionOrder);
    }
    public List<ProductionOrder> findAllProductionOrders(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return productionOrderRepository.findAll();
        } else {
            return productionOrderRepository.search(stringFilter);
        }
    }
}
