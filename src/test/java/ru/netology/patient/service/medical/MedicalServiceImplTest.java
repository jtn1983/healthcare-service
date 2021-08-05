package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

class MedicalServiceImplTest {
    private static PatientInfoFileRepository patientInfoFileRepository;
    private static SendAlertServiceImpl sendAlertService;
    private static MedicalService medicalService;

    @BeforeEach
    void setUp() {
        patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById("1")).thenReturn(new PatientInfo("1", "Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));
        sendAlertService = Mockito.mock(SendAlertServiceImpl.class);
        medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
    }

    @Test
    void checkBloodPressure_needHelp() {
        medicalService.checkBloodPressure("1", new BloodPressure(185,78));
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 1, need help", argumentCaptor.getValue());
    }

    @Test
    void checkTemperature_needHelp() {
        medicalService.checkTemperature("1", new BigDecimal("40.1"));
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 1, need help", argumentCaptor.getValue());
    }

    @Test
    void checkBloodPressureAndTemperatureOk() {
        medicalService.checkBloodPressure("1", new BloodPressure(125, 78));
        medicalService.checkTemperature("1", new BigDecimal("36.6"));
        Mockito.verify(sendAlertService, Mockito.times(0)).send("Warning, patient with id: 1, need help");
    }
}