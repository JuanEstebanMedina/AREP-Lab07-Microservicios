package com.twitter.api.service;

import com.twitter.api.dto.PostDTO;
import com.twitter.api.entity.Post;
import com.twitter.api.entity.Stream;
import com.twitter.api.entity.User;
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

    /**
     * Fetch every post ordered by creation date (descending).
     *
     * @return list of posts mapped to DTOs
     */
    @Transactional(readOnly = true)
    public List<PostDTO> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Locate a single post by its identifier.
     *
     * @param id post identifier
     * @return post as DTO
     * @throws ResourceNotFoundException when the post does not exist
     */
    @Transactional(readOnly = true)
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado con id: " + id));
        return convertToDTO(post);
    }

    /**
     * Create a post for a specific user/stream pair supplied by the payload.
     *
     * @param postDTO payload containing userId, streamId and content
     * @return persisted post as DTO
     */
    @Transactional
    public PostDTO createPost(PostDTO postDTO) {
        User user = userRepository.findById(postDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + postDTO.getUserId()));

        Stream stream = streamRepository.findById(postDTO.getStreamId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Stream not found with id: " + postDTO.getStreamId()));

        Post post = new Post();
        post.setContenido(postDTO.getContenido());
        post.setUser(user);
        post.setStream(stream);

        Post savedPost = postRepository.save(post);
        return convertToDTO(savedPost);
    }

    /**
     * Create a post on behalf of the authenticated user. If the user does not
     * exist locally it is provisioned using the provided username.
     *
     * @param username resolved identifier from Cognito session
     * @param postDTO  payload containing streamId and content
     * @return persisted post as DTO
     */
    @Transactional
    public PostDTO createPostAuthenticated(String username, PostDTO postDTO) {
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(username + "@cognito");
            newUser.setPassword("[cognito]");
            return userRepository.save(newUser);
        });

        Stream stream = streamRepository.findById(postDTO.getStreamId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Stream not found with id: " + postDTO.getStreamId()));

        Post post = new Post();
        post.setContenido(postDTO.getContenido());
        post.setUser(user);
        post.setStream(stream);

        Post savedPost = postRepository.save(post);
        return convertToDTO(savedPost);
    }

    /**
     * Delete a post using its identifier.
     *
     * @param id post identifier
     */
    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }

    /**
     * Retrieve posts created by a particular user ordered by creation date.
     *
     * @param userId user identifier
     * @return list of posts mapped to DTOs
     */
    @Transactional(readOnly = true)
    public List<PostDTO> getPostsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve posts that belong to a specific stream ordered by creation date.
     *
     * @param streamId stream identifier
     * @return list of posts mapped to DTOs
     */
    @Transactional(readOnly = true)
    public List<PostDTO> getPostsByStreamId(Long streamId) {
        if (!streamRepository.existsById(streamId)) {
            throw new ResourceNotFoundException("Stream not found with id: " + streamId);
        }
        return postRepository.findByStreamIdOrderByCreatedAtDesc(streamId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUser().getId());
        dto.setStreamId(post.getStream().getId());
        dto.setContenido(post.getContenido());
        dto.setUsername(post.getUser().getUsername());
        dto.setStreamNombre(post.getStream().getNombre());
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }
}
