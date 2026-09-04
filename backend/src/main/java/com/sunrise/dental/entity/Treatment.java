package com.sunrise.dental.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="treatments")
public class Treatment extends BaseEntity {
    @Column(nullable=false,unique=true,length=20) private String code;
    @Column(nullable=false,length=100) private String name;
    @Column(nullable=false,precision=10,scale=2) private BigDecimal cost;
    @Column(nullable=false) private boolean active=true;
    public String getCode(){return code;} public void setCode(String v){code=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public BigDecimal getCost(){return cost;} public void setCost(BigDecimal v){cost=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
