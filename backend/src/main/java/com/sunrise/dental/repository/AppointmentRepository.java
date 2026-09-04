package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByAppointmentNumber(String number);

    List<Appointment> findByAppointmentTimeBetweenOrderByAppointmentTime(LocalDateTime from, LocalDateTime to);

    List<Appointment> findByDentistIdAndAppointmentTimeBetween(Long dentistId, LocalDateTime from, LocalDateTime to);
}
