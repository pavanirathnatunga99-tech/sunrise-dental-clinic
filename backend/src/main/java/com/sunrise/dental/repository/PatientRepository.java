package com.sunrise.dental.repository;
import com.sunrise.dental.entity.Patient; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface PatientRepository extends JpaRepository<Patient,Long>{ List<Patient> findTop20ByFullNameContainingIgnoreCaseOrPhoneContaining(String name,String phone); }
