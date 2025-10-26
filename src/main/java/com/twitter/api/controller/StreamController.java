package com.twitter.api.controller;

import com.twitter.api.dto.PostDTO;
import com.twitter.api.dto.StreamDTO;
import com.twitter.api.service.PostService;
import com.twitter.api.service.StreamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StreamController {
    
    private final StreamService streamService;
    private final PostService postService;
    
    @GetMapping
    public ResponseEntity<List<StreamDTO>> getAllStreams() {
        return ResponseEntity.ok(streamService.getAllStreams());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StreamDTO> getStreamById(@PathVariable Long id) {
        return ResponseEntity.ok(streamService.getStreamById(id));
    }
    
    @PostMapping
    public ResponseEntity<StreamDTO> createStream(@Valid @RequestBody StreamDTO streamDTO) {
        StreamDTO createdStream = streamService.createStream(streamDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStream);
    }
    
    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostDTO>> getPostsByStreamId(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostsByStreamId(id));
    }
}
