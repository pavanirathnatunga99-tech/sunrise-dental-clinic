package com.sunrise.dental.entity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name="patients")
public class Patient extends BaseEntity {
    @Column(nullable=false,length=100) private String fullName;
    private LocalDate dateOfBirth;
    @Column(length=20) private String gender;
    @Column(nullable=false,length=25) private String phone;
    @Column(length=120) private String email;
    @Column(length=255) private String address;
    @Column(length=500) private String medicalNotes;
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){dateOfBirth=v;}
    public String getGender(){return gender;} public void setGender(String v){gender=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getMedicalNotes(){return medicalNotes;} public void setMedicalNotes(String v){medicalNotes=v;}
}
