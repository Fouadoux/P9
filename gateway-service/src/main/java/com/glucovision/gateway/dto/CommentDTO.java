package com.glucovision.gateway.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private String observation;
    private LocalDateTime date;
}
