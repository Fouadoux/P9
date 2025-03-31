package com.glucovision.diabetesriskservice.service;

import com.glucovision.diabetesriskservice.dto.NoteDto;
import com.glucovision.diabetesriskservice.dto.PatientDto;
import com.glucovision.diabetesriskservice.model.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RiskServiceTest {

    private NoteDataService noteDataService;
    private PatientDataService patientDataService;
    private RiskService riskService;

    @BeforeEach
    public void setUp() {
        noteDataService = mock(NoteDataService.class);
        patientDataService = mock(PatientDataService.class);
        riskService = new RiskService(noteDataService, patientDataService);
    }

    @Test
    public void testEvaluateRiskLevel_shouldReturnNone_whenNoTriggerTerms() {
        // Given
        Long patientId = 1L;
        PatientDto patient = new PatientDto(patientId, LocalDate.now().minusYears(45), "M");
        List<NoteDto> notes = List.of(
                new NoteDto(patientId, "Le patient va bien."),
                new NoteDto(patientId, "Aucun symptôme notable.")
        );

        when(patientDataService.getPatient(patientId)).thenReturn(patient);
        when(noteDataService.getNoteDtoList(patientId)).thenReturn(notes);

        // When
        RiskLevel result = riskService.evaluateRiskLevel(patientId);

        // Then
        assertEquals(RiskLevel.NONE, result);
    }

    @Test
    public void testEvaluateRiskLevel_shouldReturnBorderline_whenAgeAbove30_and2To5Triggers() {
        // Given
        Long patientId = 2L;
        PatientDto patient = new PatientDto(patientId, LocalDate.now().minusYears(45), "F");
        List<NoteDto> notes = List.of(
                new NoteDto(patientId, "Le patient présente des vertiges et du cholestérol élevé."),
                new NoteDto(patientId, "Aucun autre problème détecté.")
        );
        // Vertiges + Cholestérol = 2 déclencheurs

        when(patientDataService.getPatient(patientId)).thenReturn(patient);
        when(noteDataService.getNoteDtoList(patientId)).thenReturn(notes);

        // When
        RiskLevel result = riskService.evaluateRiskLevel(patientId);

        // Then
        assertEquals(RiskLevel.BORDERLINE, result);
    }

    @Test
    public void testEvaluateRiskLevel_shouldReturnInDanger_whenMaleUnder30_and3To4Triggers() {
        // Given
        Long patientId = 3L;
        PatientDto patient = new PatientDto(patientId, LocalDate.now().minusYears(25), "M");
        List<NoteDto> notes = List.of(
                new NoteDto(patientId, "Le patient est fumeur, présente une taille anormale et a du cholestérol.")
                // Déclencheurs : FUMEUR, TAILLE, ANORMAL, CHOLESTÉROL => 4 déclencheurs
        );

        when(patientDataService.getPatient(patientId)).thenReturn(patient);
        when(noteDataService.getNoteDtoList(patientId)).thenReturn(notes);

        // When
        RiskLevel result = riskService.evaluateRiskLevel(patientId);

        // Then
        assertEquals(RiskLevel.IN_DANGER, result);
    }

    @Test
    public void testEvaluateRiskLevel_shouldReturnEarlyOnset_whenMaleUnder30_and5OrMoreTriggers() {
        // Given
        Long patientId = 4L;
        PatientDto patient = new PatientDto(patientId, LocalDate.now().minusYears(28), "Male");
        List<NoteDto> notes = List.of(
                new NoteDto(patientId, "Fumeur avec un taux élevé de cholestérol et une taille anormale."),
                new NoteDto(patientId, "Présente aussi des vertiges et une microalbumine détectée.")
                // FUMEUR, CHOLESTÉROL, TAILLE, ANORMAL, VERTIGES, MICROALBUMINE → 6 déclencheurs
        );

        when(patientDataService.getPatient(patientId)).thenReturn(patient);
        when(noteDataService.getNoteDtoList(patientId)).thenReturn(notes);

        // When
        RiskLevel result = riskService.evaluateRiskLevel(patientId);

        // Then
        assertEquals(RiskLevel.EARLY_ONSET, result);
    }

    @Test
    public void testEvaluateRiskLevel_shouldReturnInDanger_whenFemaleUnder30_and4To6Triggers() {
        // Given
        Long patientId = 5L;
        PatientDto patient = new PatientDto(patientId, LocalDate.now().minusYears(29), "F");
        List<NoteDto> notes = List.of(
                new NoteDto(patientId, "La patiente est fumeuse, présente une taille anormale."),
                new NoteDto(patientId, "Cholestérol élevé et microalbumine détectée.")
                // FUMEUSE, TAILLE, ANORMAL, CHOLESTÉROL, MICROALBUMINE → 5 déclencheurs
        );

        when(patientDataService.getPatient(patientId)).thenReturn(patient);
        when(noteDataService.getNoteDtoList(patientId)).thenReturn(notes);

        // When
        RiskLevel result = riskService.evaluateRiskLevel(patientId);

        // Then
        assertEquals(RiskLevel.IN_DANGER, result);
    }

    @Test
    public void testEvaluateRiskLevel_shouldReturnEarlyOnset_whenFemaleUnder30_and7OrMoreTriggers() {
        // Given
        Long patientId = 6L;
        PatientDto patient = new PatientDto(patientId, LocalDate.now().minusYears(25), "Female");
        List<NoteDto> notes = List.of(
                new NoteDto(patientId, "Fumeuse avec cholestérol, vertiges, taille anormale."),
                new NoteDto(patientId, "Réaction suspecte, microalbumine et anticorps détectés.")
                // FUMEUSE, CHOLESTÉROL, VERTIGES, TAILLE, ANORMAL, RÉACTION, MICROALBUMINE, ANTICORPS → 8 déclencheurs
        );

        when(patientDataService.getPatient(patientId)).thenReturn(patient);
        when(noteDataService.getNoteDtoList(patientId)).thenReturn(notes);

        // When
        RiskLevel result = riskService.evaluateRiskLevel(patientId);

        // Then
        assertEquals(RiskLevel.EARLY_ONSET, result);
    }


}
