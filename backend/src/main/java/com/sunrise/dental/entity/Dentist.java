package com.sunrise.dental.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="dentists")
public class Dentist extends BaseEntity {
    @Column(nullable=false,length=100) private String fullName;
    @Column(nullable=false,length=100) private String specialization;
    @Column(nullable=false,precision=10,scale=2) private BigDecimal consultationFee;
    @Column(nullable=false) private boolean active=true;
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public String getSpecialization(){return specialization;} public void setSpecialization(String v){specialization=v;}
    public BigDecimal getConsultationFee(){return consultationFee;} public void setConsultationFee(BigDecimal v){consultationFee=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
