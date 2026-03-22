package com.minirippling.mini_rippling.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.*;


@Getter
@Setter
public class RegisterRequest {
    public UUID employeeId;
    public String password;
}
