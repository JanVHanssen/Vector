package org.example.service;

import org.example.model.Customer;
import org.example.model.Description;
import org.example.repository.CustomerRepository;
import org.example.repository.ProductionOrderRepository;
import org.example.repository.RewindOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final RewindOrderRepository rewindOrderRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           ProductionOrderRepository productionOrderRepository,
                           RewindOrderRepository rewindOrderRepository) {
        this.customerRepository = customerRepository;
        this.productionOrderRepository = productionOrderRepository;
        this.rewindOrderRepository = rewindOrderRepository;
    }

    public Customer saveCustomer(Customer customer) {
        Optional<Customer> existingCustomer = customerRepository.findByName(customer.getName());
        if (existingCustomer.isPresent() && !existingCustomer.get().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Customer already exists");
        }
        return customerRepository.save(customer);
    }

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public void deleteCustomer(Customer customer) {
        if (productionOrderRepository.existsByCustomer(customer) || rewindOrderRepository.existsByCustomer(customer)) {
            throw new IllegalStateException("Cannot delete customer with existing orders");
        }
        customerRepository.delete(customer);
    }
}