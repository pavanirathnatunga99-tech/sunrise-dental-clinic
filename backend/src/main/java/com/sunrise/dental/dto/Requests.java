package com.sunrise.dental.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.Set;

public final class Requests {
    private Requests() {
    }

    public record Login(@NotBlank String username, @NotBlank String password) {
    }

    public record Signup(@NotBlank @Size(min = 3, max = 60) String username,
            @NotBlank @Size(min = 8, max = 128) String password, @NotBlank @Size(max = 100) String fullName,
            @NotBlank String role) {
    }

    public record PatientInput(@NotBlank @Size(max = 100) String fullName, @Past LocalDate dateOfBirth, String gender,
            @NotBlank @Size(max = 25) String phone, @Email String email, String address, String medicalNotes) {
    }

    public record DentistInput(@NotBlank String fullName, @NotBlank String specialization,
            @NotNull @DecimalMin("0.00") BigDecimal consultationFee, boolean active) {
    }

    public record TreatmentInput(@NotBlank String code, @NotBlank String name,
            @NotNull @DecimalMin("0.00") BigDecimal cost, boolean active) {
    }

    public record AppointmentInput(@NotNull Long patientId, @NotNull Long dentistId,
            @NotNull @FutureOrPresent LocalDateTime appointmentTime, @Min(15) @Max(240) Integer durationMinutes,
            String notes, Set<Long> treatmentIds) {
    }
}
