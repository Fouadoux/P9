package com.glucovision.diabetesriskservice.service;

import com.glucovision.diabetesriskservice.dto.NoteDto;
import com.glucovision.diabetesriskservice.dto.PatientDto;
import com.glucovision.diabetesriskservice.exception.PatientNotFoundException;
import com.glucovision.diabetesriskservice.model.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service responsible for evaluating diabetes risk level for a given patient.
 * <p>
 * Combines data from the patient profile and medical notes to determine a risk category.
 */
@Slf4j
@Service
@AllArgsConstructor
public class RiskService {

    private final NoteDataService noteService;
    private final PatientDataService patientService;

    private static final Set<String> TRIGGER_TERMS = Set.of(
            "HÉMOGLOBINE A1C", "MICROALBUMINE", "TAILLE", "POIDS", "FUMEUR",
            "FUMEUSE", "ANORMAL", "CHOLESTÉROL", "VERTIGES", "RECHUTE",
            "RÉACTION", "ANTICORPS"
    );

    /**
     * Calculates the patient's age based on their birth date.
     *
     * @param patientDto the patient data
     * @return the age in years
     */
    public int calculateAge(PatientDto patientDto) {
        return Period.between(patientDto.getBirthDate(), LocalDate.now()).getYears();
    }

    /**
     * Calculates the number of risk trigger terms found in the patient's medical notes.
     *
     * @param noteDtoList list of notes to evaluate
     * @return total number of matched trigger terms
     */
    public int calculateRisk(List<NoteDto> noteDtoList) {
        int risk = 0;
        for (NoteDto noteDto : noteDtoList) {
            Set<String> matchedTriggers = new HashSet<>();
            String upperCase = noteDto.getComments().toUpperCase();
            for (String trigger : TRIGGER_TERMS) {
                if (upperCase.contains(trigger)) {
                    matchedTriggers.add(trigger);
                }
            }
            risk += matchedTriggers.size();
        }
        return risk;
    }

    /**
     * Evaluates the diabetes risk level for a patient by combining demographic data and note analysis.
     *
     * @param id the patient identifier
     * @return the evaluated {@link RiskLevel}
     */
    public RiskLevel evaluateRiskLevel(String id) {
        log.info("🔍 Starting risk evaluation for patientId={}", id);

        PatientDto patientDto = patientService.getPatient(id);
        if (patientDto == null) {
            log.warn("❌ Patient not found: {}", id);
            throw new PatientNotFoundException("Patient not found with ID: " + id);
        }

        List<NoteDto> noteDtoList = noteService.getNoteDtoList(id);
        if (noteDtoList.isEmpty()) {
            log.info("ℹ️ No notes found for patientId={}, returning NONE", id);
            return RiskLevel.NONE;
        }

        int age = calculateAge(patientDto);
        int triggerCount = calculateRisk(noteDtoList);

        log.debug("PatientId={} | Age={} | Trigger count={}", id, age, triggerCount);

        RiskLevel riskLevel;

        if (triggerCount == 0) {
            riskLevel = RiskLevel.NONE;
        } else if (triggerCount >= 2 && triggerCount <= 5 && age > 30) {
            riskLevel = RiskLevel.BORDERLINE;
        } else if (isInDanger(patientDto, age, triggerCount)) {
            riskLevel = RiskLevel.IN_DANGER;
        } else if (isEarlyOnset(patientDto, age, triggerCount)) {
            riskLevel = RiskLevel.EARLY_ONSET;
        } else {
            riskLevel = RiskLevel.NONE;
        }

        log.info("✅ Risk level evaluated for patientId={} -> {}", id, riskLevel);
        return riskLevel;
    }

    private boolean isInDanger(PatientDto patientDto, int age, int triggerCount) {
        String gender = patientDto.getGender().toUpperCase();
        if (gender.equals("M") || gender.equals("MALE")) {
            return age < 30 && triggerCount >= 3 && triggerCount < 5;
        }
        if (gender.equals("F") || gender.equals("FEMALE")) {
            return age < 30 && triggerCount >= 4 && triggerCount < 7;
        }
        return age > 30 && (triggerCount == 6 || triggerCount == 7);
    }

    private boolean isEarlyOnset(PatientDto patientDto, int age, int triggerCount) {
        String gender = patientDto.getGender().toUpperCase();
        if ((gender.equals("M") || gender.equals("MALE")) && age < 30 && triggerCount >= 5) {
            return true;
        }
        if ((gender.equals("F") || gender.equals("FEMALE")) && age < 30 && triggerCount >= 7) {
            return true;
        }
        return age > 30 && triggerCount >= 8;
    }
}
