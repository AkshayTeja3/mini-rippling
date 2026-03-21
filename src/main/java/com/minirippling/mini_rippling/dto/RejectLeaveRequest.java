package com.minirippling.mini_rippling.dto;
import java.util.*;

import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class RejectLeaveRequest {
    public UUID rejectedById;
    public String rejectionNote;

}
