package com.glucovision.noteservice.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Document(collection = "history")
public class Note {

    public Note(Long patientId, String comments, LocalDateTime creationDate) {
        this.patientId = patientId;
        this.comments = comments;
        this.creationDate = creationDate;
    }

    public Note() {
    }

    @Id
    private String id;

    private Long patientId;

    private String comments;

    private LocalDateTime  creationDate;

    private LocalDateTime modificationDate;
}
