package com.minirippling.mini_rippling.repository;


import com.minirippling.mini_rippling.domain.Employee;
import com.minirippling.mini_rippling.domain.Department;
import com.minirippling.mini_rippling.domain.AttendanceRecord;
import com.minirippling.mini_rippling.domain.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;
public interface EmployeeRepository extends JpaRepository<Employee, UUID>{
    Optional<Employee> findByEmail(String email);
    List<Employee> findByIsActiveTrue();
}
