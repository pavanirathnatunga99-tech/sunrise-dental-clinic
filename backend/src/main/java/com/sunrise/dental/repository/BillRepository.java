package com.sunrise.dental.repository;
import com.sunrise.dental.entity.Bill; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface BillRepository extends JpaRepository<Bill,Long>{ Optional<Bill> findByAppointmentId(Long id); Optional<Bill> findByInvoiceNumber(String number); }
