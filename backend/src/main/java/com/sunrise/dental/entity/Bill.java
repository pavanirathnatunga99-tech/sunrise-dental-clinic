package com.sunrise.dental.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="bills")
public class Bill extends BaseEntity {
    public enum PaymentStatus { UNPAID, PAID }
    @Column(nullable=false,unique=true,length=24) private String invoiceNumber;
    @OneToOne(optional=false) private Appointment appointment;
    @Column(nullable=false,precision=10,scale=2) private BigDecimal consultationFee;
    @Column(nullable=false,precision=10,scale=2) private BigDecimal treatmentCost;
    @Column(nullable=false,precision=10,scale=2) private BigDecimal total;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private PaymentStatus paymentStatus=PaymentStatus.UNPAID;
    public String getInvoiceNumber(){return invoiceNumber;} public void setInvoiceNumber(String v){invoiceNumber=v;}
    public Appointment getAppointment(){return appointment;} public void setAppointment(Appointment v){appointment=v;}
    public BigDecimal getConsultationFee(){return consultationFee;} public void setConsultationFee(BigDecimal v){consultationFee=v;}
    public BigDecimal getTreatmentCost(){return treatmentCost;} public void setTreatmentCost(BigDecimal v){treatmentCost=v;}
    public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal v){total=v;}
    public PaymentStatus getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(PaymentStatus v){paymentStatus=v;}
}
