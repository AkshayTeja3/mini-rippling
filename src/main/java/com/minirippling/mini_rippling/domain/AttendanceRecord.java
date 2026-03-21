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
@Table(name = "attendance_records", indexes = {
        @Index(name = "idx_attendance_employee_date",
                columnList = "employee_id, work_date DESC"),
        @Index(name = "idx_attendance_date",
                columnList = "work_date"),
        @Index(name = "idx_attendance_status",
                columnList = "status")
},
        uniqueConstraints = {
                // ONE RECORD PER EMPLOYEE PER DAY
                // enforced at database level — not just application level
                @UniqueConstraint(
                        name = "uq_attendance_per_day",
                        columnNames = {"employee_id", "work_date"}
                )
        })
@Getter
@Setter
@NoArgsConstructor
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // WHICH EMPLOYEE, WHICH DAY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    // PUNCH DATA
    // nullable — employee might miss punch in or punch out
    @Column(name = "clock_in")
    private LocalDateTime clockIn;

    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    // WHAT HAPPENED THIS DAY
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(name = "late_minutes", nullable = false)
    private int lateMinutes = 0;

    @Column(name = "overtime_minutes", nullable = false)
    private int overtimeMinutes = 0;

    // IDEMPOTENCY KEY
    // client generates this UUID before sending punch request
    // if network fails and client retries → same UUID arrives
    // UNIQUE constraint fires → duplicate rejected safely
    // no double punch. no duplicate record.
    @Column(name = "idempotency_key", unique = true)
    private UUID idempotencyKey;

    @Column(name = "timezone", nullable = false, length = 60)
    private String timezone = "Asia/Kolkata";

    // MANUAL CORRECTION FIELDS
    // HR or admin can fix wrong records
    @Column(name = "is_corrected", nullable = false)
    private boolean isCorrected = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corrected_by_id")
    private Employee correctedBy;

    @Column(name = "correction_note", columnDefinition = "TEXT")
    private String correctionNote;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ATTENDANCE STATUS ENUM
    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        HALF_DAY,
        ON_LEAVE,
        HOLIDAY,
        WEEKEND
    }
}
