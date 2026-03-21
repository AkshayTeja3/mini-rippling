// PayrollEntryRepository.java
package com.minirippling.mini_rippling.repository;

import com.minirippling.mini_rippling.domain.Employee;
import com.minirippling.mini_rippling.domain.PayrollEntry;
import com.minirippling.mini_rippling.domain.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PayrollEntryRepository extends JpaRepository<PayrollEntry, UUID> {
    List<PayrollEntry> findByPayrollRun(PayrollRun payrollRun);
    Optional<PayrollEntry> findByPayrollRunAndEmployee(PayrollRun payrollRun, Employee employee);
}