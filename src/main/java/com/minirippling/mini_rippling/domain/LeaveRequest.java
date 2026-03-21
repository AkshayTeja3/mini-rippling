package com.minirippling.mini_rippling.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leave_requests", indexes = {
        @Index(name = "idx_leave_employee",
                columnList = "employee_id"),
        @Index(name = "idx_leave_dates",
                columnList = "start_date, end_date"),
        @Index(name = "idx_leave_status",
                columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // WHO IS APPLYING
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // WHAT KIND OF LEAVE
    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false, length = 20)
    private LeaveType leaveType;

    // WHEN
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // actual working days — not just endDate - startDate
    // weekends and holidays inside the range don't count
    @Column(name = "total_days", nullable = false)
    private int totalDays;

    // WHY — nullable, not every company requires a reason
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    // APPROVAL WORKFLOW
    // starts as PENDING, moves to APPROVED or REJECTED
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LeaveStatus status = LeaveStatus.PENDING;

    // who approved or rejected — nullable until decision is made
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private Employee approvedBy;

    // when decision was made
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    // why it was rejected — nullable until rejected
    @Column(name = "rejection_note", columnDefinition = "TEXT")
    private String rejectionNote;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ENUMS
    public enum LeaveType {
        CASUAL,
        SICK,
        EARNED,
        UNPAID,
        MATERNITY,
        PATERNITY
    }

    public enum LeaveStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }
}