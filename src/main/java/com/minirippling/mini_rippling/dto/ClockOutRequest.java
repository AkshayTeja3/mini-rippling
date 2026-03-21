package com.minirippling.mini_rippling.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class ClockOutRequest {
    public UUID employeeId ;
    public UUID idempotencyKey;
    public LocalDateTime clockOut;

}
