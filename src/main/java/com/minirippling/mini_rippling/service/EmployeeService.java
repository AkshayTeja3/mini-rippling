package com.minirippling.mini_rippling.service;

import com.minirippling.mini_rippling.domain.Employee;

import com.minirippling.mini_rippling.domain.EmploymentHistory;

import com.minirippling.mini_rippling.domain.Department;

import com.minirippling.mini_rippling.repository.EmployeeRepository;

import com.minirippling.mini_rippling.repository.EmploymentHistoryRepository;

import com.minirippling.mini_rippling.repository.DepartmentRepository;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service

public class EmployeeService {

    // Spring automatically injects these

    private final EmployeeRepository employeeRepository;

    private final EmploymentHistoryRepository employmentHistoryRepository;

    private final DepartmentRepository departmentRepository;

    // constructor injection — Spring calls this automatically

    public EmployeeService(EmployeeRepository employeeRepository,

                           EmploymentHistoryRepository employmentHistoryRepository,

                           DepartmentRepository departmentRepository) {

        this.employeeRepository = employeeRepository;

        this.employmentHistoryRepository = employmentHistoryRepository;

        this.departmentRepository = departmentRepository;

    }

    @Transactional

    public Employee hireEmployee(String firstName, String lastName,
                                 String email, String phone,
                                 String role, BigDecimal salary,
                                 UUID departmentId, UUID managerId) {

        // STEP 1 — check email doesn't already exist

        if (employeeRepository.findByEmail(email).isPresent()) {

            throw new RuntimeException("Employee with email " + email + " already exists");

        }

        // STEP 2 — check department exists

        Department department;
        department = departmentRepository.findById(departmentId).orElseThrow(() -> {
            return new RuntimeException("Department not found");
        });

        // STEP 3 — create the employee

        Employee employee = new Employee();

        employee.setFirstName(firstName);

        employee.setLastName(lastName);

        employee.setEmail(email);

        employee.setPhone(phone);

        employee.setDepartment(department);

        employee.setDateOfJoining(LocalDate.now());

        employee.setActive(true);

        employee.setEmployeeCode(generateEmployeeCode());

        // save to database

        Employee savedEmployee = employeeRepository.save(employee);

        EmploymentHistory employmentHistory = new EmploymentHistory();

        employmentHistory.setEmployee(savedEmployee);

        employmentHistory.setRole("SDE_1");

        employmentHistory.setSalaryAmount(new BigDecimal("500000"));

        employmentHistory.setSalaryCurrency("INR");

        employmentHistory.setSalaryType(EmploymentHistory.SalaryType.MONTHLY);

        employmentHistory.setEffectiveFrom(LocalDate.now());

        employmentHistory.setChangedBy(savedEmployee);

        employmentHistoryRepository.save(employmentHistory);

        return savedEmployee;

    }

    // generates EMP-0001, EMP-0002 etc

    private String generateEmployeeCode() {

        long count = employeeRepository.count();

        return String.format("EMP-%04d", count + 1);

    }

    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    public List<Employee> getAllActiveEmployees() {
        return employeeRepository.findByIsActiveTrue();
    }

}