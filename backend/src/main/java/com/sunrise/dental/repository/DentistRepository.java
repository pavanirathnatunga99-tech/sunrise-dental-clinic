package com.sunrise.dental.repository;
import com.sunrise.dental.entity.Dentist; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface DentistRepository extends JpaRepository<Dentist,Long>{ List<Dentist> findByActiveTrueOrderByFullName(); }
