package com.example.careercraft.service.impl;

import com.example.careercraft.entity.Customer;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerFinderService {

    private final CustomerRepository customerRepository;

    Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + customerId));
    }
}
