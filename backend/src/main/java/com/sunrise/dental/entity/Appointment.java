package com.sunrise.dental.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name="appointments", uniqueConstraints={@UniqueConstraint(name="uk_dentist_slot",columnNames={"dentist_id","appointment_time"})})
public class Appointment extends BaseEntity {
    public enum Status { SCHEDULED, COMPLETED, CANCELLED }
    @Column(nullable=false,unique=true,length=24) private String appointmentNumber;
    @ManyToOne(optional=false) private Patient patient;
    @ManyToOne(optional=false) private Dentist dentist;
    @Column(nullable=false) private LocalDateTime appointmentTime;
    @Column(nullable=false) private Integer durationMinutes=30;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status=Status.SCHEDULED;
    @Column(length=500) private String notes;
    @ManyToMany @JoinTable(name="appointment_treatments",joinColumns=@JoinColumn(name="appointment_id"),inverseJoinColumns=@JoinColumn(name="treatment_id"))
    private Set<Treatment> treatments=new LinkedHashSet<>();
    public String getAppointmentNumber(){return appointmentNumber;} public void setAppointmentNumber(String v){appointmentNumber=v;}
    public Patient getPatient(){return patient;} public void setPatient(Patient v){patient=v;}
    public Dentist getDentist(){return dentist;} public void setDentist(Dentist v){dentist=v;}
    public LocalDateTime getAppointmentTime(){return appointmentTime;} public void setAppointmentTime(LocalDateTime v){appointmentTime=v;}
    public Integer getDurationMinutes(){return durationMinutes;} public void setDurationMinutes(Integer v){durationMinutes=v;}
    public Status getStatus(){return status;} public void setStatus(Status v){status=v;}
    public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
    public Set<Treatment> getTreatments(){return treatments;} public void setTreatments(Set<Treatment> v){treatments=v;}
}
