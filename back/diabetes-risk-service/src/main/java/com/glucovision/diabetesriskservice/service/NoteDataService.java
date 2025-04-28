package com.glucovision.diabetesriskservice.service;

import com.glucovision.diabetesriskservice.client.NoteClient;
import com.glucovision.diabetesriskservice.dto.NoteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteDataService {

    private final NoteClient noteClient;

    public List<NoteDto> getNoteDtoList(String id) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID du patient ne peut pas être null ou vide");
        }
        return noteClient.getNoteListe(id);
    }

}
