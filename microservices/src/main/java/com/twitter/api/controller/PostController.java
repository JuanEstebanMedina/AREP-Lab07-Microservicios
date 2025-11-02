package com.twitter.api.controller;

import com.twitter.api.dto.PostDTO;
import com.twitter.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostDTO postDTO,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.core.user.OAuth2User principal) {
        if (principal != null) {
            String username = principal.getAttribute("cognito:username");
            if (username == null) {
                username = principal.getAttribute("preferred_username");
            }
            if (username == null) {
                username = principal.getAttribute("email");
            }
            if (username == null) {
                username = principal.getName();
            }
            PostDTO createdPost = postService.createPostAuthenticated(username, postDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } else if (postDTO.getUserId() != null) {
            PostDTO createdPost = postService.createPost(postDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }
}
