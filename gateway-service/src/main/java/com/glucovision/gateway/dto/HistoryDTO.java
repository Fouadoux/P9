package com.glucovision.gateway.dto;

import lombok.Data;

import java.util.List;

@Data
public class HistoryDTO {
    private String id;
    private String patientId;
    private List<CommentDTO> comments;
}
