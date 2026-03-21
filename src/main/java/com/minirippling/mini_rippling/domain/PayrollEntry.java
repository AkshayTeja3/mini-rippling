package com.minirippling.mini_rippling.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payroll_entries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_entry_per_run",
                        columnNames = {"payroll_run_id", "employee_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class PayrollEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_run_id", nullable = false)
    private PayrollRun payrollRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // snapshot of salary at time of this run
    @Column(name = "base_salary", nullable = false, precision = 14, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "total_working_days", nullable = false)
    private int totalWorkingDays;

    @Column(name = "days_worked", nullable = false)
    private int daysWorked;

    @Column(name = "leave_days", nullable = false)
    private int leaveDays;

    @Column(name = "unpaid_leave_days", nullable = false)
    private int unpaidLeaveDays;

    @Column(name = "overtime_hours", precision = 6, scale = 2)
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "earnings", nullable = false, precision = 14, scale = 2)
    private BigDecimal earnings;

    @Column(name = "deductions", nullable = false, precision = 14, scale = 2)
    private BigDecimal deductions;

    @Column(name = "bonus", precision = 14, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "net_pay", nullable = false, precision = 14, scale = 2)
    private BigDecimal netPay;

    // full itemized breakdown — every rupee explained
    @Column(name = "breakdown_json", columnDefinition = "TEXT")
    private String breakdownJson;

    // file path or URL to generated payslip
    @Column(name = "payslip_url")
    private String payslipUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EntryStatus status = EntryStatus.PENDING;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum EntryStatus {
        PENDING,
        PROCESSED,
        FAILED,
        SKIPPED
    }
}