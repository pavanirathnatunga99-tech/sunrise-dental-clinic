package com.sunrise.dental.service;

import com.sunrise.dental.dto.Requests.*;
import com.sunrise.dental.entity.*;
import com.sunrise.dental.exception.*;
import com.sunrise.dental.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class ClinicService {
    private final PatientRepository patients;
    private final DentistRepository dentists;
    private final TreatmentRepository treatments;
    private final AppointmentRepository appointments;
    private final BillRepository bills;

    public ClinicService(PatientRepository p, DentistRepository d, TreatmentRepository t, AppointmentRepository a,
            BillRepository b) {
        patients = p;
        dentists = d;
        treatments = t;
        appointments = a;
        bills = b;
    }

    public Patient savePatient(Long id, PatientInput x) {
        Patient p = id == null ? new Patient()
                : patients.findById(id).orElseThrow(() -> new NotFoundException("Patient not found."));
        p.setFullName(x.fullName());
        p.setDateOfBirth(x.dateOfBirth());
        p.setGender(x.gender());
        p.setPhone(x.phone());
        p.setEmail(x.email());
        p.setAddress(x.address());
        p.setMedicalNotes(x.medicalNotes());
        return patients.save(p);
    }

    @Transactional(readOnly = true)
    public List<Patient> patientList(String q) {
        return q == null || q.isBlank() ? patients.findAll()
                : patients.findTop20ByFullNameContainingIgnoreCaseOrPhoneContaining(q, q);
    }

    public Dentist saveDentist(Long id, DentistInput x) {
        Dentist d = id == null ? new Dentist()
                : dentists.findById(id).orElseThrow(() -> new NotFoundException("Dentist not found."));
        d.setFullName(x.fullName());
        d.setSpecialization(x.specialization());
        d.setConsultationFee(x.consultationFee());
        d.setActive(x.active());
        return dentists.save(d);
    }

    public Treatment saveTreatment(Long id, TreatmentInput x) {
        Treatment t = id == null ? new Treatment()
                : treatments.findById(id).orElseThrow(() -> new NotFoundException("Treatment not found."));
        t.setCode(x.code().toUpperCase());
        t.setName(x.name());
        t.setCost(x.cost());
        t.setActive(x.active());
        return treatments.save(t);
    }

    public Appointment createAppointment(AppointmentInput x) {
        return saveAppointment(new Appointment(), x);
    }

    public Appointment updateAppointment(String number, AppointmentInput x) {
        Appointment a = findAppointment(number);
        if (a.getStatus() == Appointment.Status.CANCELLED)
            throw new ConflictException("A cancelled appointment cannot be edited.");
        return saveAppointment(a, x);
    }

    @SuppressWarnings("null")
    private Appointment saveAppointment(Appointment a, AppointmentInput x) {
        Patient p = patients.findById(x.patientId()).orElseThrow(() -> new NotFoundException("Patient not found."));
        Dentist d = dentists.findById(x.dentistId())
                .filter(dentist -> dentist != null && dentist.isActive())
                .orElseThrow(() -> new NotFoundException("Active dentist not found."));
        int duration = x.durationMinutes() == null ? 30 : x.durationMinutes();
        LocalDateTime start = x.appointmentTime(), end = start.plusMinutes(duration);
        boolean conflict = appointments
                .findByDentistIdAndAppointmentTimeBetween(d.getId(), start.toLocalDate().atStartOfDay(),
                        start.toLocalDate().plusDays(1).atStartOfDay())
                .stream().filter(existing -> existing.getStatus() != Appointment.Status.CANCELLED)
                .filter(existing -> a.getId() == null || !existing.getId().equals(a.getId()))
                .anyMatch(existing -> start
                        .isBefore(existing.getAppointmentTime().plusMinutes(existing.getDurationMinutes()))
                        && end.isAfter(existing.getAppointmentTime()));
        if (conflict)
            throw new ConflictException("This dentist already has an appointment during the selected time.");
        a.setPatient(p);
        a.setDentist(d);
        a.setAppointmentTime(start);
        a.setDurationMinutes(duration);
        a.setNotes(x.notes());
        if (a.getAppointmentNumber() == null)
            a.setAppointmentNumber("APT-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
                    + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        Set<Treatment> selected = new LinkedHashSet<>();
        if (x.treatmentIds() != null)
            selected.addAll(treatments.findAllById((java.lang.Iterable<Long>) x.treatmentIds()));
        a.setTreatments(selected);
        return appointments.save(a);
    }

    public Appointment cancel(String number) {
        Appointment a = findAppointment(number);
        a.setStatus(Appointment.Status.CANCELLED);
        return appointments.save(a);
    }

    public Appointment complete(String number) {
        Appointment a = findAppointment(number);
        if (a.getStatus() == Appointment.Status.CANCELLED)
            throw new ConflictException("A cancelled appointment cannot be completed.");
        a.setStatus(Appointment.Status.COMPLETED);
        return appointments.save(a);
    }

    @Transactional(readOnly = true)
    public Appointment findAppointment(String n) {
        return appointments.findByAppointmentNumber(n)
                .orElseThrow(() -> new NotFoundException("Appointment " + n + " was not found."));
    }

    @Transactional(readOnly = true)
    public List<Appointment> appointmentList(LocalDate date) {
        LocalDate d = date == null ? LocalDate.now() : date;
        return appointments.findByAppointmentTimeBetweenOrderByAppointmentTime(d.atStartOfDay(),
                d.plusDays(1).atStartOfDay());
    }

    public Bill generateBill(String appointmentNumber) {
        Appointment a = findAppointment(appointmentNumber);
        return bills.findByAppointmentId(a.getId()).orElseGet(() -> {
            @SuppressWarnings("null")
            BigDecimal treatment = a.getTreatments().stream()
                    .map(Treatment::getCost)
                    .filter(cost -> cost != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Bill b = new Bill();
            b.setInvoiceNumber("INV-" + System.currentTimeMillis());
            b.setAppointment(a);
            b.setConsultationFee(a.getDentist().getConsultationFee());
            b.setTreatmentCost(treatment);
            b.setTotal(b.getConsultationFee().add(treatment));
            return bills.save(b);
        });
    }

    public Bill pay(String invoice) {
        Bill b = bills.findByInvoiceNumber(invoice).orElseThrow(() -> new NotFoundException("Invoice not found."));
        b.setPaymentStatus(Bill.PaymentStatus.PAID);
        return bills.save(b);
    }

    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public Map<String, Object> dashboard() {
        LocalDate today = LocalDate.now();
        List<Appointment> list = appointmentList(today);
        BigDecimal revenue = bills.findAll().stream()
                .filter(b -> b != null && b.getPaymentStatus() == Bill.PaymentStatus.PAID).map(Bill::getTotal)
                .reduce(BigDecimal.ZERO, (BigDecimal t, BigDecimal c) -> t.add(c != null ? c : BigDecimal.ZERO));
        return Map.of("date", today, "appointmentsToday", list.size(), "scheduled",
                list.stream().filter(a -> a.getStatus() == Appointment.Status.SCHEDULED).count(), "completed",
                list.stream().filter(a -> a.getStatus() == Appointment.Status.COMPLETED).count(), "patients",
                patients.count(), "dentists", dentists.count(), "paidRevenue", revenue, "upcoming",
                list.stream().filter(a -> a.getAppointmentTime().isAfter(LocalDateTime.now())).limit(5).toList());
    }
}
