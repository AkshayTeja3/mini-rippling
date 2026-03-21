package com.minirippling.mini_rippling.controller;

import com.minirippling.mini_rippling.domain.LeaveRequest;
import com.minirippling.mini_rippling.dto.ApplyLeaveRequest;
import com.minirippling.mini_rippling.dto.ApproveLeaveRequest;
import com.minirippling.mini_rippling.dto.RejectLeaveRequest;
import com.minirippling.mini_rippling.service.LeaveService;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/apply")
    public LeaveRequest applyLeave(@RequestBody ApplyLeaveRequest request) {
        return leaveService.applyLeave(
                request.getEmployeeId(),
                LeaveRequest.LeaveType.valueOf(request.getLeaveType()),
                request.getStartDate(),
                request.getEndDate(),
                request.getReason()
        );
    }

    @PutMapping("/{id}/approve")
    public LeaveRequest approveLeave(@PathVariable UUID id,
                                     @RequestBody ApproveLeaveRequest request) {
        return leaveService.approveLeave(id, request.getApprovedById());

    }
    @PutMapping("/{id}/reject")
    public LeaveRequest rejectLeave(@PathVariable UUID id,
                                    @RequestBody RejectLeaveRequest request) {
        return leaveService.rejectLeave(
                id,request.getRejectedById(),request.getRejectionNote());
    }

}