package com.glucovision.historyservice.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private String observation;
    private LocalDateTime date;

    public Comment(String observation) {
        this.observation = observation;
        this.date = LocalDateTime.now();
    }
}
