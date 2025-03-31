package com.glucovision.diabetesriskservice.service;

import com.glucovision.diabetesriskservice.dto.NoteDto;
import com.glucovision.diabetesriskservice.dto.PatientDto;
import com.glucovision.diabetesriskservice.dto.RiskDto;
import com.glucovision.diabetesriskservice.model.RiskLevel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class RiskService {
    private NoteDataService noteService;
    private final PatientDataService patientService;

    private static final Set<String> TRIGGER_TERMS = Set.of(
            "HÉMOGLOBINE A1C",
            "MICROALBUMINE",
            "TAILLE",
            "POIDS",
            "FUMEUR",
            "FUMEUSE",
            "ANORMAL",
            "CHOLESTÉROL",
            "VERTIGES",
            "RECHUTE",
            "RÉACTION",
            "ANTICORPS"
    );


    public int calculateAge(PatientDto patientDto) {
        LocalDate birthDate = patientDto.getBirthDate();
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public int calculateRisk(List<NoteDto> noteDtoList) {

        int risk = 0;
        for (NoteDto noteDto : noteDtoList) {
            Set<String> matchedTriggers= new HashSet<>();
           String upperCase= noteDto.getComments().toUpperCase();
           for (String trigger : TRIGGER_TERMS) {
               if (upperCase.contains(trigger)) {
                   matchedTriggers.add(trigger);
               }
           }
            risk += matchedTriggers.size();
        }
        return risk;
    }

    public RiskLevel evaluateRiskLevel(Long id) {
        List<NoteDto> noteDtoList = noteService.getNoteDtoList(id);
        PatientDto patientDto = patientService.getPatient(id);
        int age = calculateAge(patientDto);
        int triggerCount = calculateRisk(noteDtoList);

        if (triggerCount==0) {
            return RiskLevel.NONE;
        }
        if (triggerCount >= 2 && triggerCount <= 5 && age > 30){
            return RiskLevel.BORDERLINE;
        }
        // Homme < 30 ans
        if (patientDto.getGender().equalsIgnoreCase("M") || patientDto.getGender().equalsIgnoreCase("Male")) {
            if (age < 30 && triggerCount >= 3 && triggerCount < 5)
                return RiskLevel.IN_DANGER;
        }

        // Femme < 30 ans
        if (patientDto.getGender().equalsIgnoreCase("F") || patientDto.getGender().equalsIgnoreCase("Female")) {
            if (age < 30 && triggerCount >= 4 && triggerCount < 7)
                return RiskLevel.IN_DANGER;
        }

        // Tous > 30 ans
        if (age > 30 && (triggerCount == 6 || triggerCount == 7)) {
            return RiskLevel.IN_DANGER;
        }

        // EARLY_ONSET
        // Homme < 30 ans
        if ((patientDto.getGender().equalsIgnoreCase("M") || patientDto.getGender().equalsIgnoreCase("Male"))
                && age < 30 && triggerCount >= 5) {
            return RiskLevel.EARLY_ONSET;
        }

        // Femme < 30 ans
        if ((patientDto.getGender().equalsIgnoreCase("F") || patientDto.getGender().equalsIgnoreCase("Female"))
                && age < 30 && triggerCount >= 7) {
            return RiskLevel.EARLY_ONSET;
        }

        // Âge > 30
        if (age > 30 && triggerCount >= 8) {
            return RiskLevel.EARLY_ONSET;
        }
        return RiskLevel.NONE;
    }


}
