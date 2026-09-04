package com.sunrise.dental.controller;
import com.sunrise.dental.dto.Requests.*; import com.sunrise.dental.entity.*; import com.sunrise.dental.repository.*; import com.sunrise.dental.service.ClinicService; import jakarta.validation.Valid; import org.springframework.format.annotation.DateTimeFormat; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.time.LocalDate; import java.util.*;

@RestController @RequestMapping("/api")
public class ClinicController {
 private final ClinicService clinic; private final DentistRepository dentists; private final TreatmentRepository treatments;
 public ClinicController(ClinicService c,DentistRepository d,TreatmentRepository t){clinic=c;dentists=d;treatments=t;}
 @GetMapping("/dashboard") public Map<String,Object> dashboard(){return clinic.dashboard();}
 @GetMapping("/patients") public List<Patient> patients(@RequestParam(required=false)String q){return clinic.patientList(q);}
 @PostMapping("/patients") public Patient createPatient(@Valid @RequestBody PatientInput x){return clinic.savePatient(null,x);}
 @PutMapping("/patients/{id}") public Patient updatePatient(@PathVariable Long id,@Valid @RequestBody PatientInput x){return clinic.savePatient(id,x);}
 @GetMapping("/dentists") public List<Dentist> dentists(){return dentists.findAll();}
 @PostMapping("/dentists") @PreAuthorize("hasRole('ADMIN')") public Dentist createDentist(@Valid @RequestBody DentistInput x){return clinic.saveDentist(null,x);}
 @PutMapping("/dentists/{id}") @PreAuthorize("hasRole('ADMIN')") public Dentist updateDentist(@PathVariable Long id,@Valid @RequestBody DentistInput x){return clinic.saveDentist(id,x);}
 @GetMapping("/treatments") public List<Treatment> treatments(){return treatments.findAll();}
 @PostMapping("/treatments") @PreAuthorize("hasRole('ADMIN')") public Treatment createTreatment(@Valid @RequestBody TreatmentInput x){return clinic.saveTreatment(null,x);}
 @PutMapping("/treatments/{id}") @PreAuthorize("hasRole('ADMIN')") public Treatment updateTreatment(@PathVariable Long id,@Valid @RequestBody TreatmentInput x){return clinic.saveTreatment(id,x);}
 @GetMapping("/appointments") public List<Appointment> appointments(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date){return clinic.appointmentList(date);}
 @GetMapping("/appointments/{number}") public Appointment appointment(@PathVariable String number){return clinic.findAppointment(number);}
 @PostMapping("/appointments") public Appointment createAppointment(@Valid @RequestBody AppointmentInput x){return clinic.createAppointment(x);}
 @PutMapping("/appointments/{number}") public Appointment updateAppointment(@PathVariable String number,@Valid @RequestBody AppointmentInput x){return clinic.updateAppointment(number,x);}
 @PatchMapping("/appointments/{number}/cancel") public Appointment cancel(@PathVariable String number){return clinic.cancel(number);}
 @PatchMapping("/appointments/{number}/complete") public Appointment complete(@PathVariable String number){return clinic.complete(number);}
 @PostMapping("/bills/appointment/{number}") public Bill bill(@PathVariable String number){return clinic.generateBill(number);}
 @PatchMapping("/bills/{invoice}/pay") public Bill pay(@PathVariable String invoice){return clinic.pay(invoice);}
}
