package com.minirippling.mini_rippling.service;

import com.minirippling.mini_rippling.domain.Employee;
import com.minirippling.mini_rippling.domain.UserAccount;
import com.minirippling.mini_rippling.repository.EmployeeRepository;
import com.minirippling.mini_rippling.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final UserAccountRepository userAccountRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(EmployeeRepository employeeRepository,
                       UserAccountRepository userAccountRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.userAccountRepository = userAccountRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }
    public UserAccount register(UUID employeeId, String password) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return createAccount(employee, password);
    }

    // login — returns JWT token
    public String login(String email, String password) {

        // STEP 1 — find employee by email
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // STEP 2 — find their user account
        UserAccount account = userAccountRepository.findByEmployee(employee)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // STEP 3 — check if account is active
        if (!account.isActive()) {
            throw new RuntimeException("Account is disabled");
        }

        // STEP 4 — verify password
        if (!passwordEncoder.matches(password, account.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // STEP 5 — update last login
        account.setLastLoginAt(LocalDateTime.now());
        userAccountRepository.save(account);

        // STEP 6 — generate and return JWT token
        return jwtService.generateToken(employee.getId(), employee.getEmail());
    }

    // create account for an employee
    public UserAccount createAccount(Employee employee, String password) {
        UserAccount account = new UserAccount();
        account.setEmployee(employee);
        account.setPasswordHash(passwordEncoder.encode(password));
        account.setActive(true);
        return userAccountRepository.save(account);
    }
}