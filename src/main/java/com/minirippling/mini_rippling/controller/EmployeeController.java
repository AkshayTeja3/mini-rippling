package com.minirippling.mini_rippling.controller;


import com.minirippling.mini_rippling.domain.Employee;
import com.minirippling.mini_rippling.dto.HireEmployeeRequest;
import com.minirippling.mini_rippling.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.math.*;
import java.util.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    @PostMapping("/hire")
    public Employee hireEmployee(@RequestBody HireEmployeeRequest request) {
        return employeeService.hireEmployee(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhoneNumber(), request.getRole(), request.getSalary(), request.getDepartmentId(), request.getManagerId());
    }
    @GetMapping
    public List<Employee> getAllActiveEmployees() {
        return employeeService.getAllActiveEmployees();
    }
    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable UUID id) {
        return employeeService.getEmployeeById(id);
    }
}
