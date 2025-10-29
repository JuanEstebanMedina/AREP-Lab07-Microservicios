package com.twitter.api.service;

import com.twitter.api.dto.StreamDTO;
import com.twitter.api.entity.Stream;
import com.twitter.api.exception.ResourceNotFoundException;
import com.twitter.api.repository.StreamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StreamService {

    private final StreamRepository streamRepository;

    /**
     * Retrieve every stream ordered by the repository default (created date desc).
     *
     * @return list of streams as DTOs
     */
    @Transactional(readOnly = true)
    public List<StreamDTO> getAllStreams() {
        return streamRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a single stream by its identifier.
     *
     * @param id stream identifier
     * @return matching stream as DTO
     * @throws ResourceNotFoundException when the stream does not exist
     */
    @Transactional(readOnly = true)
    public StreamDTO getStreamById(Long id) {
        Stream stream = streamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stream not found with id: " + id));
        return convertToDTO(stream);
    }

    /**
     * Persist a new stream with basic metadata.
     *
     * @param streamDTO payload with name and description
     * @return created stream as DTO
     */
    @Transactional
    public StreamDTO createStream(StreamDTO streamDTO) {
        Stream stream = new Stream();
        stream.setNombre(streamDTO.getNombre());
        stream.setDescripcion(streamDTO.getDescripcion());

        Stream savedStream = streamRepository.save(stream);
        return convertToDTO(savedStream);
    }

    private StreamDTO convertToDTO(Stream stream) {
        StreamDTO dto = new StreamDTO();
        dto.setId(stream.getId());
        dto.setNombre(stream.getNombre());
        dto.setDescripcion(stream.getDescripcion());
        dto.setCreatedAt(stream.getCreatedAt());
        dto.setTotalPosts(stream.getPosts() != null ? stream.getPosts().size() : 0);
        return dto;
    }
}
