package com.minirippling.mini_rippling.dto;

import java.math.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HireEmployeeRequest {
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String email;
    public String role;
    public BigDecimal salary;
    public UUID departmentId;
    public UUID managerId;



}
