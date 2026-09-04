package com.sunrise.dental.entity;
import jakarta.persistence.*;

@Entity @Table(name="staff_users")
public class User extends BaseEntity {
    public enum Role { ADMIN, RECEPTIONIST, DENTIST }
    @Column(nullable=false,unique=true,length=60) private String username;
    @Column(nullable=false) private String password;
    @Column(nullable=false,length=100) private String fullName;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
    @Column(nullable=false) private boolean enabled=true;
    public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getPassword(){return password;} public void setPassword(String v){password=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public Role getRole(){return role;} public void setRole(Role v){role=v;}
    public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;}
}
