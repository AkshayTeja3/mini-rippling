package com.minirippling.mini_rippling.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.*;
import java.time.*;

@Getter
@Setter
public class RunPayrollRequest {
    public LocalDate periodStart;
    public LocalDate periodEnd;
    public String runType;
    public UUID initiatedById;



}
