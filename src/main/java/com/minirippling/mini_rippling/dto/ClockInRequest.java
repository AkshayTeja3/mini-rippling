package com.minirippling.mini_rippling.dto;

import java.time.LocalDateTime;
import java.util.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ClockInRequest {
    public UUID employeeId ;
    public UUID idempotencyKey;
    public LocalDateTime clockIn;


}
