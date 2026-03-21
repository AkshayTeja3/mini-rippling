package com.minirippling.mini_rippling.service;

import com.minirippling.mini_rippling.domain.AttendanceRecord;
import com.minirippling.mini_rippling.domain.Employee;
import com.minirippling.mini_rippling.repository.AttendanceRecordRepository;
import com.minirippling.mini_rippling.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.UUID;

@Service
public class AttendanceService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private Object shiftEnd;

    public AttendanceService(EmployeeRepository employeeRepository,
                             AttendanceRecordRepository attendanceRecordRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @Transactional
    public AttendanceRecord clockIn(UUID employeeId,
                                    UUID idempotencyKey,
                                    LocalDateTime clockInTime) {

        // STEP 1 — find employee, throw error if not found
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // STEP 2 — check if already clocked in today
        Optional<AttendanceRecord> existing = attendanceRecordRepository
                .findByEmployeeAndWorkDate(employee, LocalDate.now());
        if (existing.isPresent()) {
            throw new RuntimeException("Already clocked in today");
        }

        // hint: use attendanceRecordRepository.findByEmployeeAndWorkDate()
        // if present → throw RuntimeException "Already clocked in today"


        // STEP 3 — check idempotency key
        Optional<AttendanceRecord> existingPunch = attendanceRecordRepository
                .findByIdempotencyKey(idempotencyKey);
        if (existingPunch.isPresent()) {
            return existingPunch.get(); // retry — return existing record
        }
        // if this UUID already exists → return existing record


        // STEP 4 — calculate late minutes
        // shift starts at 9:00am
        LocalTime shiftStart = LocalTime.of(9, 0);
        int lateMinutes = 0;
        if (clockInTime.toLocalTime().isAfter(shiftStart)) {
            lateMinutes = (int) java.time.Duration.between(shiftStart,
                    clockInTime.toLocalTime()).toMinutes();
        }
        // hint: if clockInTime.toLocalTime() is after shiftStart
        // calculate the difference in minutes


        // STEP 5 — create and save attendance record
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployee(employee);
        record.setWorkDate(LocalDate.now());
        record.setClockIn(clockInTime);
        record.setStatus(AttendanceRecord.AttendanceStatus.PRESENT);
        record.setLateMinutes(lateMinutes);
        record.setIdempotencyKey(idempotencyKey);
        record.setTimezone("Asia/Kolkata");

        return attendanceRecordRepository.save(record);

    }
    @Transactional
    public AttendanceRecord clockOut(UUID employeeId, LocalDateTime clockOutTime) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        AttendanceRecord record = attendanceRecordRepository
                .findByEmployeeAndWorkDate(employee, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No clock in found for today"));

        if (record.getClockOut() != null) {
            throw new RuntimeException("Already clocked out today");
        }

        LocalTime shiftEnd = LocalTime.of(18, 0);
        int overtimeMinutes = 0;
        if (clockOutTime.toLocalTime().isAfter(shiftEnd)) {
            overtimeMinutes = (int) java.time.Duration.between(shiftEnd,
                    clockOutTime.toLocalTime()).toMinutes();
        }

        record.setClockOut(clockOutTime);
        record.setOvertimeMinutes(overtimeMinutes);
        return attendanceRecordRepository.save(record);
    }
}