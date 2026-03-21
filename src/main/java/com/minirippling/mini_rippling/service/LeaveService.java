package com.minirippling.mini_rippling.service;

import com.minirippling.mini_rippling.domain.AttendanceRecord;
import com.minirippling.mini_rippling.domain.Employee;
import com.minirippling.mini_rippling.domain.LeaveRequest;
import com.minirippling.mini_rippling.repository.AttendanceRecordRepository;
import com.minirippling.mini_rippling.repository.EmployeeRepository;
import com.minirippling.mini_rippling.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class LeaveService {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public LeaveService(EmployeeRepository employeeRepository,
                        LeaveRequestRepository leaveRequestRepository,
                        AttendanceRecordRepository attendanceRecordRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.attendanceRecordRepository= attendanceRecordRepository;
    }

    @Transactional
    public LeaveRequest applyLeave(UUID employeeId,
                                   LeaveRequest.LeaveType leaveType,
                                   LocalDate startDate,
                                   LocalDate endDate,
                                   String reason) {

        // STEP 1 — find employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // STEP 2 — calculate total days
        int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // STEP 3 — create leave request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setStartDate(startDate);
        leaveRequest.setEndDate(endDate);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setReason(reason);
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);
        leaveRequest.setTotalDays(totalDays);


        // try filling this yourself
        // hint: new LeaveRequest(), set all fields, status = PENDING
        // then save using leaveRequestRepository.save()

        return leaveRequestRepository.save(leaveRequest); // replace with saved leave request
    }
    @Transactional
    public LeaveRequest approveLeave(UUID leaveRequestId, UUID approvedById) {

        // STEP 1 — find leave request
        LeaveRequest leave = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        // STEP 2 — check status is PENDING
        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new RuntimeException("Leave request is already " + leave.getStatus());
        }

        // STEP 3 — set approved fields
        Employee approvedBy = employeeRepository.findById(approvedById)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        leave.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        leave.setApprovedBy(approvedBy);
        leave.setApprovedAt(LocalDateTime.now());

        // STEP 4 — loop through dates and update attendance
        // STEP 4 — loop through dates and update attendance
        LocalDate current = leave.getStartDate();
        while (!current.isAfter(leave.getEndDate())) {

            // skip weekends
            if (current.getDayOfWeek() != DayOfWeek.SATURDAY
                    && current.getDayOfWeek() != DayOfWeek.SUNDAY) {

                LocalDate finalCurrent = current;
                AttendanceRecord attendance = attendanceRecordRepository.findByEmployeeAndWorkDate(leave.getEmployee(), current)
                        .orElseGet(() -> {
                            // no record exists — create one
                            AttendanceRecord newRecord = new AttendanceRecord();
                            newRecord.setEmployee(leave.getEmployee());
                            newRecord.setWorkDate(finalCurrent);
                            newRecord.setTimezone("Asia/Kolkata");
                            return newRecord;
                        });

                attendance.setStatus(AttendanceRecord.AttendanceStatus.ON_LEAVE);
                attendanceRecordRepository.save(attendance);
            }
            current = current.plusDays(1);
        }
        // we'll add this next

        return leaveRequestRepository.save(leave);
    }
    @Transactional
    public LeaveRequest rejectLeave(UUID leaveRequestId, UUID rejectedById, String rejectionNote) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new RuntimeException("Leave request is already " + leave.getStatus());
        }

        Employee rejectedBy = employeeRepository.findById(rejectedById)
                .orElseThrow(() -> new RuntimeException("Rejector not found"));

        leave.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        leave.setApprovedBy(rejectedBy);
        leave.setRejectedAt(LocalDateTime.now());
        leave.setRejectionNote(rejectionNote);

        return leaveRequestRepository.save(leave);
    }

}