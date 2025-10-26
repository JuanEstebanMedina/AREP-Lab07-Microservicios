package com.twitter.api.service;

import com.twitter.api.dto.PostDTO;
import com.twitter.api.entity.Post;
import com.twitter.api.entity.Stream;
import com.twitter.api.entity.Usuario;
import com.twitter.api.exception.ResourceNotFoundException;
import com.twitter.api.repository.PostRepository;
import com.twitter.api.repository.StreamRepository;
import com.twitter.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final StreamRepository streamRepository;
    
    @Transactional(readOnly = true)
    public List<PostDTO> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado con id: " + id));
        return convertToDTO(post);
    }
    
    @Transactional
    public PostDTO createPost(PostDTO postDTO) {
        Usuario usuario = userRepository.findById(postDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + postDTO.getUsuarioId()));
        
        Stream stream = streamRepository.findById(postDTO.getStreamId())
                .orElseThrow(() -> new ResourceNotFoundException("Stream no encontrado con id: " + postDTO.getStreamId()));
        
        Post post = new Post();
        post.setContenido(postDTO.getContenido());
        post.setUsuario(usuario);
        post.setStream(stream);
        
        Post savedPost = postRepository.save(post);
        return convertToDTO(savedPost);
    }
    
    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post no encontrado con id: " + id);
        }
        postRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<PostDTO> getPostsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + userId);
        }
        return postRepository.findByUsuarioIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PostDTO> getPostsByStreamId(Long streamId) {
        if (!streamRepository.existsById(streamId)) {
            throw new ResourceNotFoundException("Stream no encontrado con id: " + streamId);
        }
        return postRepository.findByStreamIdOrderByCreatedAtDesc(streamId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setUsuarioId(post.getUsuario().getId());
        dto.setStreamId(post.getStream().getId());
        dto.setContenido(post.getContenido());
        dto.setUsername(post.getUsuario().getUsername());
        dto.setStreamNombre(post.getStream().getNombre());
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }
}
