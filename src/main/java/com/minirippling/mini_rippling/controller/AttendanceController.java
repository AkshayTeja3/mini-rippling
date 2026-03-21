package com.minirippling.mini_rippling.controller;

import com.minirippling.mini_rippling.domain.AttendanceRecord;
import com.minirippling.mini_rippling.dto.ClockInRequest;
import com.minirippling.mini_rippling.dto.ClockOutRequest;
import com.minirippling.mini_rippling.service.AttendanceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService){

        this.attendanceService=attendanceService;
    }
    @PostMapping("/clockin")
     public AttendanceRecord getAttendanceRecord(@RequestBody ClockInRequest request){
         return attendanceService.clockIn(
                 request.getEmployeeId(),
                 request.getIdempotencyKey(),
                 request.getClockIn()
         );

    }

    @PostMapping("/clockout")
    public AttendanceRecord getAttendanceRecord(@RequestBody ClockOutRequest request){
        return attendanceService.clockOut(
                request.getEmployeeId(),
                request.getClockOut()
        );
    }

}
