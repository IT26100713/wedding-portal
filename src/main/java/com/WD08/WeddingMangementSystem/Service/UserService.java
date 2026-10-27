package com.WD08.WeddingMangementSystem.Service;

import com.WD08.WeddingMangementSystem.DTO.UserRegistrationDto;
import com.WD08.WeddingMangementSystem.DTO.UserUpdateDto;
import com.WD08.WeddingMangementSystem.Repository.UserRepository;
import com.WD08.WeddingMangementSystem.entity.Customer;
import com.WD08.WeddingMangementSystem.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- REGISTER NEW USER ---
    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email address is already registered.");
        }

        Customer customer = Customer.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phoneNumber(dto.getPhoneNumber())
                .build();

        return userRepository.save(customer);
    }

    // --- GET ALL USERS ---
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // --- GET USER BY ID ---
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    // --- GET USER BY EMAIL ---
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    // --- UPDATE USER ---
    public void updateUser(Long id, UserUpdateDto dto) {
        User user = getUserById(id);

        if (!user.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email address is already in use by another account.");
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userRepository.save(user);
    }

    // --- DELETE USER ---
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
