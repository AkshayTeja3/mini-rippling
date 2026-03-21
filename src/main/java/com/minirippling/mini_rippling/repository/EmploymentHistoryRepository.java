package com.minirippling.mini_rippling.repository;

import com.minirippling.mini_rippling.domain.Employee;
import com.minirippling.mini_rippling.domain.EmploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, UUID> {

    // current active salary record
    Optional<EmploymentHistory> findByEmployeeAndEffectiveToIsNull(Employee employee);

    // full salary history
    List<EmploymentHistory> findByEmployee(Employee employee);

    // salary on a specific past date
    @Query("SELECT e FROM EmploymentHistory e WHERE e.employee = :employee " +
            "AND e.effectiveFrom <= :date " +
            "AND (e.effectiveTo > :date OR e.effectiveTo IS NULL)")
    Optional<EmploymentHistory> findByEmployeeAndDate(
            @Param("employee") Employee employee,
            @Param("date") LocalDate date
    );
}