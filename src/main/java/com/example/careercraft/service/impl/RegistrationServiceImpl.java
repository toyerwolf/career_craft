package com.example.careercraft.service.impl;

import com.example.careercraft.entity.Customer;
import com.example.careercraft.entity.Role;
import com.example.careercraft.entity.User;
import com.example.careercraft.exception.AlreadyExistException;
import com.example.careercraft.repository.UserRepository;
import com.example.careercraft.req.UserRegistrationReq;
import com.example.careercraft.service.AbstractRegistrationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationServiceImpl extends AbstractRegistrationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Transactional
    @Override
    public void register(UserRegistrationReq request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistException("Username is already taken");
        }
        User user=new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLocked(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.USER);
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setSurname(request.getSurname());
        customer.setRegisteredAt(LocalDateTime.now());
        customer.setAddress(request.getAddress());
        user.setCustomer(customer);
        userRepository.save(user);
//        sendConfirmationEmail(request.getEmail());

    }
}
