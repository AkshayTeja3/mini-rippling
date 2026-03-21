package com.minirippling.mini_rippling.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "employment_history", indexes = {
        @Index(name = "idx_employment_history_lookup",
                columnList = "employee_id, effective_from DESC")
})
@Getter
@Setter
@NoArgsConstructor
public class EmploymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // WHICH EMPLOYEE THIS RECORD BELONGS TO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // ROLE AND SALARY AT THIS POINT IN TIME
    @Column(name = "role", nullable = false, length = 150)
    private String role;

    // BigDecimal for money — never use float or double for currency
    // float/double have rounding errors — you don't want ₹49999.99 instead of ₹50000
    @Column(name = "salary_amount", nullable = false,
            precision = 14, scale = 2)
    private BigDecimal salaryAmount;

    @Column(name = "salary_currency", nullable = false, length = 3)
    private String salaryCurrency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(name = "salary_type", nullable = false, length = 10)
    private SalaryType salaryType;

    // VERSIONING COLUMNS
    // when this record became active
    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    // when this record stopped being active
    // NULL = currently active record
    // when salary changes:
    //   step 1 → set this to today on current row
    //   step 2 → insert new row with effectiveFrom = today
    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    // WHO MADE THIS CHANGE AND WHY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id", nullable = false)
    private Employee changedBy;

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    // this row is never updated after insert
    // createdAt tells you exactly when the change was recorded
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // SALARY TYPE ENUM
    public enum SalaryType {
        MONTHLY,
        HOURLY
    }
}