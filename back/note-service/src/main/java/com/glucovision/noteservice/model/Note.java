package com.glucovision.noteservice.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Document(collection = "note")
public class Note {

    public Note(String patientId, String comments, LocalDateTime creationDate) {
        this.patientId = patientId;
        this.comments = comments;
        this.creationDate = creationDate;
    }

    public Note() {
    }

    @Id
    private String id;

    private String patientId;

    private String comments;

    private LocalDateTime  creationDate;

    private LocalDateTime modificationDate;
}
