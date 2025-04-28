package com.glucovision.diabetesriskservice.client;

import com.glucovision.diabetesriskservice.dto.NoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "note-service", url = "${note-service.url}")

public interface NoteClient {

    @GetMapping("/api/notes/patient/{id}")
    List<NoteDto> getNoteListe(@PathVariable String id);
}
