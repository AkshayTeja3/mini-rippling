// PayrollRunRepository.java
package com.minirippling.mini_rippling.repository;

import com.minirippling.mini_rippling.domain.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PayrollRunRepository extends JpaRepository<PayrollRun, UUID> {
    Optional<PayrollRun> findByIdempotencyKey(String idempotencyKey);
}