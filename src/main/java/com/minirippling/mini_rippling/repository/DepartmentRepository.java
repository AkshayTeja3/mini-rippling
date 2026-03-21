package com.minirippling.mini_rippling.repository;

import com.minirippling.mini_rippling.domain.Department;
import com.minirippling.mini_rippling.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.*;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    Optional<Department> findByName(String name);
}
