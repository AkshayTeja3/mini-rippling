package com.minirippling.mini_rippling.repository;

import com.minirippling.mini_rippling.domain.Employee;
import com.minirippling.mini_rippling.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface UserAccountRepository extends JpaRepository<UserAccount,UUID> {
    Optional<UserAccount> findByEmployee(Employee employee);

}
