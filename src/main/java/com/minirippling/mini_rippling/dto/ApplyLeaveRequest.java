package com.minirippling.mini_rippling.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.*;

import java.util.*;

@Getter
@Setter
public class ApplyLeaveRequest {
    public UUID employeeId ;
    public String leaveType;
    public LocalDate startDate;
    public LocalDate endDate;
    public String reason;

}
