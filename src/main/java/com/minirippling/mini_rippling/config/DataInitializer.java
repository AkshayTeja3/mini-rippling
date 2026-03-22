package com.minirippling.mini_rippling.config;

import com.minirippling.mini_rippling.domain.Department;
import com.minirippling.mini_rippling.domain.Employee;
import com.minirippling.mini_rippling.repository.DepartmentRepository;
import com.minirippling.mini_rippling.repository.EmployeeRepository;
import com.minirippling.mini_rippling.service.AuthService;
import com.minirippling.mini_rippling.service.EmployeeService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements ApplicationRunner {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final AuthService authService;

    public DataInitializer(DepartmentRepository departmentRepository,
                           EmployeeRepository employeeRepository,
                           EmployeeService employeeService,
                           AuthService authService) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.authService = authService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // only seed if database is empty
        if (departmentRepository.count() > 0) {
            return; // already seeded — skip
        }

        // create department
        Department dept = new Department();
        dept.setName("Engineering");
        dept.setDescription("Software Engineering Department");
        Department savedDept = departmentRepository.save(dept);

        // create admin employee
        Employee admin = employeeService.hireEmployee(
                "Akshay", "Teja",
                "teja@company.com", "9876543210",
                "Software Engineer", new BigDecimal("50000"),
                savedDept.getId(), null
        );

        // create login account
        authService.createAccount(admin, "password123");

        System.out.println("✅ Database seeded successfully");
        System.out.println("✅ Login: teja@company.com / password123");
    }
}