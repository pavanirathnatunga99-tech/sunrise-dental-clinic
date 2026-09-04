package com.sunrise.dental.service;

import com.sunrise.dental.dto.Requests.*;
import com.sunrise.dental.entity.*;
import com.sunrise.dental.exception.ConflictException;
import com.sunrise.dental.exception.NotFoundException;
import com.sunrise.dental.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicServiceTest {

    @Mock
    private PatientRepository patients;

    @Mock
    private DentistRepository dentists;

    @Mock
    private TreatmentRepository treatments;

    @Mock
    private AppointmentRepository appointments;

    @Mock
    private BillRepository bills;

    @InjectMocks
    private ClinicService service;

    @Test
    void savePatientSetsAndSavesPatientDetails() {
        PatientInput input = new PatientInput(
                "Jane Doe",
                LocalDate.of(1994, 3, 12),
                "F",
                "5551234567",
                "jane@example.com",
                "12 Maple St",
                "Needs checkup");

        Patient saved = new Patient();
        saved.setId(10L);

        when(patients.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setId(10L);
            return patient;
        });

        Patient result = service.savePatient(null, input);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getFullName()).isEqualTo("Jane Doe");
        assertThat(result.getPhone()).isEqualTo("5551234567");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        verify(patients).save(any(Patient.class));
    }

    @Test
    void createAppointmentCreatesAppointmentWithSelectedTreatment() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFullName("John Smith");

        Dentist dentist = new Dentist();
        dentist.setId(2L);
        dentist.setFullName("Dr. Lee");
        dentist.setConsultationFee(new BigDecimal("120.00"));
        dentist.setActive(true);

        Treatment treatment = new Treatment();
        treatment.setId(9L);
        treatment.setName("Cleaning");
        treatment.setCost(new BigDecimal("80.00"));
        treatment.setActive(true);

        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0)
                .withNano(0);
        AppointmentInput input = new AppointmentInput(
                1L,
                2L,
                appointmentTime,
                30,
                "Routine visit",
                Set.of(9L));

        when(patients.findById(1L)).thenReturn(Optional.of(patient));
        when(dentists.findById(2L)).thenReturn(Optional.of(dentist));
        when(treatments.findAllById((Iterable<Long>) Set.of(9L))).thenReturn(List.of(treatment));
        when(appointments.findByDentistIdAndAppointmentTimeBetween(eq(2L), any(), any())).thenReturn(List.of());
        when(appointments.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setId(7L);
            appointment.setAppointmentNumber("APT-TEST-123");
            return appointment;
        });

        Appointment result = service.createAppointment(input);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getPatient()).isSameAs(patient);
        assertThat(result.getDentist()).isSameAs(dentist);
        assertThat(result.getTreatments()).containsExactly(treatment);
        assertThat(result.getAppointmentNumber()).isEqualTo("APT-TEST-123");
        verify(appointments).save(any(Appointment.class));
    }

    @Test
    void generateBillCreatesInvoiceWhenNoExistingBillExists() {
        Dentist dentist = new Dentist();
        dentist.setConsultationFee(new BigDecimal("125.00"));

        Treatment treatment = new Treatment();
        treatment.setCost(new BigDecimal("90.00"));

        Appointment appointment = new Appointment();
        appointment.setId(21L);
        appointment.setDentist(dentist);
        appointment.setTreatments(Set.of(treatment));
        appointment.setAppointmentNumber("APT-423");

        when(appointments.findByAppointmentNumber("APT-423")).thenReturn(Optional.of(appointment));
        when(bills.findByAppointmentId(21L)).thenReturn(Optional.empty());
        when(bills.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill bill = invocation.getArgument(0);
            bill.setId(31L);
            bill.setInvoiceNumber("INV-123456");
            return bill;
        });

        Bill result = service.generateBill("APT-423");

        assertThat(result.getId()).isEqualTo(31L);
        assertThat(result.getConsultationFee()).isEqualByComparingTo("125.00");
        assertThat(result.getTreatmentCost()).isEqualByComparingTo("90.00");
        assertThat(result.getTotal()).isEqualByComparingTo("215.00");
        verify(bills).save(any(Bill.class));
    }

    @Test
    void dashboardReturnsCountsAndPaidRevenue() {
        Appointment scheduled = new Appointment();
        scheduled.setStatus(Appointment.Status.SCHEDULED);
        scheduled.setAppointmentTime(LocalDateTime.now().plusHours(1));

        Appointment completed = new Appointment();
        completed.setStatus(Appointment.Status.COMPLETED);
        completed.setAppointmentTime(LocalDateTime.now().minusHours(2));

        when(appointments.findByAppointmentTimeBetweenOrderByAppointmentTime(any(), any()))
                .thenReturn(List.of(scheduled, completed));
        when(patients.count()).thenReturn(12L);
        when(dentists.count()).thenReturn(3L);

        Bill paid = new Bill();
        paid.setPaymentStatus(Bill.PaymentStatus.PAID);
        paid.setTotal(new BigDecimal("250.00"));

        Bill unpaid = new Bill();
        unpaid.setPaymentStatus(Bill.PaymentStatus.UNPAID);
        unpaid.setTotal(new BigDecimal("80.00"));

        when(bills.findAll()).thenReturn(List.of(paid, unpaid));

        Map<String, Object> dashboard = service.dashboard();

        assertThat(dashboard.get("date")).isInstanceOf(LocalDate.class);
        assertThat(dashboard.get("appointmentsToday")).isEqualTo(2);
        assertThat(dashboard.get("scheduled")).isEqualTo(1L);
        assertThat(dashboard.get("completed")).isEqualTo(1L);
        assertThat(dashboard.get("patients")).isEqualTo(12L);
        assertThat(dashboard.get("dentists")).isEqualTo(3L);
        assertThat((BigDecimal) dashboard.get("paidRevenue")).isEqualByComparingTo("250.00");
        assertThat(dashboard.get("upcoming")).isNotNull();
    }

    @Test
    void createAppointmentRejectsOverlappingDentistBooking() {
        Patient patient = new Patient(); patient.setId(1L);
        Dentist dentist = new Dentist(); dentist.setId(2L); dentist.setActive(true);
        LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(10).withMinute(15).withSecond(0).withNano(0);
        Appointment existing = new Appointment();
        existing.setId(5L); existing.setAppointmentTime(start.minusMinutes(15));
        existing.setDurationMinutes(30); existing.setStatus(Appointment.Status.SCHEDULED);
        when(patients.findById(1L)).thenReturn(Optional.of(patient));
        when(dentists.findById(2L)).thenReturn(Optional.of(dentist));
        when(appointments.findByDentistIdAndAppointmentTimeBetween(eq(2L), any(), any()))
                .thenReturn(List.of(existing));
        AppointmentInput input = new AppointmentInput(1L, 2L, start, 30, "Overlap", Set.of());

        assertThatThrownBy(() -> service.createAppointment(input))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already has an appointment");
        verify(appointments, never()).save(any());
    }

    @Test
    void findAppointmentReportsUnknownNumber() {
        when(appointments.findByAppointmentNumber("APT-MISSING")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findAppointment("APT-MISSING"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Appointment APT-MISSING was not found.");
    }

    @Test
    void cancelChangesStatusWithoutDeletingAppointment() {
        Appointment appointment = new Appointment(); appointment.setStatus(Appointment.Status.SCHEDULED);
        when(appointments.findByAppointmentNumber("APT-1")).thenReturn(Optional.of(appointment));
        when(appointments.save(appointment)).thenReturn(appointment);
        assertThat(service.cancel("APT-1").getStatus()).isEqualTo(Appointment.Status.CANCELLED);
        verify(appointments).save(appointment);
        verify(appointments, never()).delete(any());
    }

    @Test
    void cancelledAppointmentCannotBeCompleted() {
        Appointment appointment = new Appointment(); appointment.setStatus(Appointment.Status.CANCELLED);
        when(appointments.findByAppointmentNumber("APT-2")).thenReturn(Optional.of(appointment));
        assertThatThrownBy(() -> service.complete("APT-2"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("cannot be completed");
        verify(appointments, never()).save(any());
    }

    @Test
    void generateBillReturnsExistingBillWithoutCreatingDuplicate() {
        Appointment appointment = new Appointment(); appointment.setId(21L);
        Bill existing = new Bill(); existing.setId(31L); existing.setInvoiceNumber("INV-EXISTING");
        when(appointments.findByAppointmentNumber("APT-423")).thenReturn(Optional.of(appointment));
        when(bills.findByAppointmentId(21L)).thenReturn(Optional.of(existing));
        assertThat(service.generateBill("APT-423")).isSameAs(existing);
        verify(bills, never()).save(any());
    }
}
