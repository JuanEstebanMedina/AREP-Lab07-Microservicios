package com.twitter.api.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twitter.api.dto.UserDTO;
import com.twitter.api.entity.User;
import com.twitter.api.exception.ResourceNotFoundException;
import com.twitter.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user no encontrado con id: " + id));
        return convertToDTO(user);
    }
    
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }
    
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user no encontrado con id: " + id));
        
        if (!user.getUsername().equals(userDTO.getUsername()) && 
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }
        
        if (!user.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }
        
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("user no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        // No incluir password en el DTO de respuesta
        return dto;
    }

    private void createDefaultAdminUser() {
        // Comprueba si ya existe un user con username 'admin'
        if (userRepository.findByUsername("admin").isEmpty()) {
            // La entidad del proyecto es `user` (no tiene campos role/enabled),
            // la construimos con el builder de Lombok y encriptamos la contraseña
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("juanito"))
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created");
        } else {
            System.out.println("Admin user already exists");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        createDefaultAdminUser();
    }
}
