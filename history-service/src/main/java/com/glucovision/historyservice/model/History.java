package com.glucovision.historyservice.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "history")
public class History {

    @Id
    private String id;

    private String patientId;

    private String comments;

    private Date date;
}
