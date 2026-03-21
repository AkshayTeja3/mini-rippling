package com.minirippling.mini_rippling.controller;

import com.minirippling.mini_rippling.domain.PayrollRun;
import com.minirippling.mini_rippling.dto.RunPayrollRequest;
import com.minirippling.mini_rippling.service.PayrollService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/run")
    public PayrollRun runPayroll(@RequestBody RunPayrollRequest request) {
        return payrollService.runPayroll(
                request.getPeriodStart(),
                request.getPeriodEnd(),
                PayrollRun.RunType.valueOf(request.getRunType()),
                request.getInitiatedById()
        );
    }
}