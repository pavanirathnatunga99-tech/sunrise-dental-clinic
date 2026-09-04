package com.sunrise.dental.repository;
import com.sunrise.dental.entity.Treatment; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface TreatmentRepository extends JpaRepository<Treatment,Long>{ List<Treatment> findByActiveTrueOrderByName(); }
