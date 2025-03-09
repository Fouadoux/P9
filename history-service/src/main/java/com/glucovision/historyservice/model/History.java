package com.glucovision.historyservice.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "history")
public class History {
    @Id
    private String id;

    private String patientId;

    private List<Comment> comments = new ArrayList<>();

    public History(String patientId) {
        this.patientId = patientId;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }
}
