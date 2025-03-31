export interface DiabetesRisk {
    patientId: number;
    riskLevel: 'NONE' | 'BORDERLINE' | 'IN_DANGER' | 'EARLY_ONSET';
  }