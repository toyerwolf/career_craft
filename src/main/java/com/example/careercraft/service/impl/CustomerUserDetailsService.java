package com.example.careercraft.service.impl;

import com.example.careercraft.entity.User;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.UserRepository;
import com.example.careercraft.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("user with username " + username + " not found"));
        return createUserPrincipal(user);
    }

    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return createUserPrincipal(user);
    }

    private UserPrincipal createUserPrincipal(User user) {
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId(user.getId());
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setEmail(user.getEmail());
        userPrincipal.setRole(user.getRole());
        userPrincipal.setLocked(user.isLocked());
        return userPrincipal;
    }
}