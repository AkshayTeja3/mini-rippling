package com.minirippling.mini_rippling.service;

import com.minirippling.mini_rippling.domain.*;
import com.minirippling.mini_rippling.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class PayrollService {

    private final PayrollRunRepository payrollRunRepository;
    private final PayrollEntryRepository payrollEntryRepository;
    private final EmployeeRepository employeeRepository;
    private final EmploymentHistoryRepository employmentHistoryRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public PayrollService(PayrollRunRepository payrollRunRepository,
                          PayrollEntryRepository payrollEntryRepository,
                          EmployeeRepository employeeRepository,
                          EmploymentHistoryRepository employmentHistoryRepository,
                          AttendanceRecordRepository attendanceRecordRepository) {
        this.payrollRunRepository = payrollRunRepository;
        this.payrollEntryRepository = payrollEntryRepository;
        this.employeeRepository = employeeRepository;
        this.employmentHistoryRepository = employmentHistoryRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @Transactional
    public PayrollRun runPayroll(LocalDate periodStart,
                                 LocalDate periodEnd,
                                 PayrollRun.RunType runType,
                                 UUID initiatedById) {

        // STEP 1 — idempotency check
        // build key like "REGULAR-2026-03"
        String idempotencyKey = runType + "-" + periodStart.getYear()
                + "-" + periodStart.getMonthValue();

        if (payrollRunRepository.findByIdempotencyKey(idempotencyKey).isPresent()) {
            throw new RuntimeException("Payroll already run for this period: "
                    + idempotencyKey);
        }

        // STEP 2 — find who initiated this
        Employee initiatedBy = employeeRepository.findById(initiatedById)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // STEP 3 — create payroll run record
        PayrollRun payrollRun = new PayrollRun();
        payrollRun.setPeriodStart(periodStart);
        payrollRun.setPeriodEnd(periodEnd);
        payrollRun.setRunType(runType);
        payrollRun.setStatus(PayrollRun.RunStatus.PROCESSING);
        payrollRun.setIdempotencyKey(idempotencyKey);
        payrollRun.setInitiatedBy(initiatedBy);
        PayrollRun savedRun = payrollRunRepository.save(payrollRun);

        // STEP 4 — get all active employees
        List<Employee> employees = employeeRepository.findByIsActiveTrue();

        // STEP 5 — process each employee
        for (Employee employee : employees) {
            try {
                processEmployeePayroll(employee, savedRun, periodStart, periodEnd);
            } catch (Exception e) {
                // one employee failing doesn't stop others
                // log and continue
                System.err.println("Failed to process payroll for: "
                        + employee.getEmail() + " — " + e.getMessage());
            }
        }

        // STEP 6 — mark run as completed
        savedRun.setStatus(PayrollRun.RunStatus.COMPLETED);
        savedRun.setProcessedAt(LocalDateTime.now());
        return payrollRunRepository.save(savedRun);
    }

    private void processEmployeePayroll(Employee employee,
                                        PayrollRun payrollRun,
                                        LocalDate periodStart,
                                        LocalDate periodEnd) {

        // STEP A — get current salary
        EmploymentHistory history = employmentHistoryRepository
                .findByEmployeeAndEffectiveToIsNull(employee)
                .orElseThrow(() -> new RuntimeException(
                        "No salary record for: " + employee.getEmail()));

        BigDecimal baseSalary = history.getSalaryAmount();

        // STEP B — get attendance for this period
        List<AttendanceRecord> attendance = attendanceRecordRepository
                .findByEmployee(employee);

        // STEP C — count days
        int totalWorkingDays = countWorkingDays(periodStart, periodEnd);
        int daysWorked = 0;
        int leaveDays = 0;
        int unpaidLeaveDays = 0;
        int overtimeMinutes = 0;

        for (AttendanceRecord record : attendance) {
            if (!record.getWorkDate().isBefore(periodStart) &&
                    !record.getWorkDate().isAfter(periodEnd)) {
                switch (record.getStatus()) {
                    case PRESENT -> daysWorked++;
                    case HALF_DAY -> daysWorked++;
                    case ON_LEAVE -> leaveDays++;
                    case ABSENT -> unpaidLeaveDays++;
                    default -> {}
                }
                overtimeMinutes += record.getOvertimeMinutes();
            }
        }

        // STEP D — calculate pay
        BigDecimal dailyRate = baseSalary
                .divide(BigDecimal.valueOf(totalWorkingDays), 2, RoundingMode.HALF_UP);

        BigDecimal earnings = dailyRate.multiply(BigDecimal.valueOf(daysWorked));
        BigDecimal deductions = dailyRate.multiply(BigDecimal.valueOf(unpaidLeaveDays));
        BigDecimal overtimePay = dailyRate
                .divide(BigDecimal.valueOf(8), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(overtimeMinutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        BigDecimal netPay = earnings.subtract(deductions).add(overtimePay);

        // STEP E — build breakdown JSON
        String breakdownJson = String.format(
                "{\"baseSalary\":%s,\"dailyRate\":%s,\"daysWorked\":%d," +
                        "\"unpaidLeaveDays\":%d,\"earnings\":%s,\"deductions\":%s," +
                        "\"overtimePay\":%s,\"netPay\":%s}",
                baseSalary, dailyRate, daysWorked, unpaidLeaveDays,
                earnings, deductions, overtimePay, netPay);

        // STEP F — create payroll entry
        PayrollEntry entry = new PayrollEntry();
        entry.setPayrollRun(payrollRun);
        entry.setEmployee(employee);
        entry.setBaseSalary(baseSalary);
        entry.setTotalWorkingDays(totalWorkingDays);
        entry.setDaysWorked(daysWorked);
        entry.setLeaveDays(leaveDays);
        entry.setUnpaidLeaveDays(unpaidLeaveDays);
        entry.setOvertimeHours(BigDecimal.valueOf(overtimeMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
        entry.setEarnings(earnings);
        entry.setDeductions(deductions);
        entry.setBonus(BigDecimal.ZERO);
        entry.setNetPay(netPay);
        entry.setBreakdownJson(breakdownJson);
        entry.setStatus(PayrollEntry.EntryStatus.PROCESSED);
        entry.setProcessedAt(LocalDateTime.now());

        payrollEntryRepository.save(entry);
    }

    // count working days excluding weekends
    private int countWorkingDays(LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate current = start;
        while (!current.isAfter(end)) {
            if (current.getDayOfWeek() != DayOfWeek.SATURDAY
                    && current.getDayOfWeek() != DayOfWeek.SUNDAY) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }
}