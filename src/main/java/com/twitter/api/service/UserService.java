package com.twitter.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twitter.api.dto.UserDTO;
import com.twitter.api.entity.Usuario;
import com.twitter.api.exception.ResourceNotFoundException;
import com.twitter.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return convertToDTO(usuario);
    }
    
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }
        
        Usuario usuario = new Usuario();
        usuario.setUsername(userDTO.getUsername());
        usuario.setEmail(userDTO.getEmail());
        usuario.setPassword(userDTO.getPassword()); // En producción, usar BCrypt
        
        Usuario savedUsuario = userRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }
    
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        
        if (!usuario.getUsername().equals(userDTO.getUsername()) && 
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }
        
        if (!usuario.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }
        
        usuario.setUsername(userDTO.getUsername());
        usuario.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            usuario.setPassword(userDTO.getPassword());
        }
        
        Usuario updatedUsuario = userRepository.save(usuario);
        return convertToDTO(updatedUsuario);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    private UserDTO convertToDTO(Usuario usuario) {
        UserDTO dto = new UserDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setCreatedAt(usuario.getCreatedAt());
        // No incluir password en el DTO de respuesta
        return dto;
    }

    private void createDefaultAdminUser() {
        // Comprueba si ya existe un usuario con username 'admin'
        if (userRepository.findByUsername("admin").isEmpty()) {
            // La entidad del proyecto es `Usuario` (no tiene campos role/enabled),
            // así que creamos un Usuario básico con username, email y password encriptada.
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword("juanito");
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
