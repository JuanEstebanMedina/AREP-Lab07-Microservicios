package com.twitter.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    
    private Long id;
    
    @NotNull(message = "El ID del usuario es requerido")
    private Long usuarioId;
    
    @NotNull(message = "El ID del stream es requerido")
    private Long streamId;
    
    @NotBlank(message = "El contenido no puede estar vacío")
    @Size(max = 140, message = "El post no puede exceder 140 caracteres")
    private String contenido;
    
    private String username;
    private String streamNombre;
    private LocalDateTime createdAt;
}
