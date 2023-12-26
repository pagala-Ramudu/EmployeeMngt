package org.ci.employeeMngt.controller;

import org.ci.employeeMngt.entity.EmployeeAttendance;
import org.ci.employeeMngt.service.EmployeeAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
public class EmployeeAttendanceController {
    @Autowired
    private EmployeeAttendanceService employeeAttendanceService;

    @PostMapping
    public ResponseEntity<EmployeeAttendance> createEmployeeAttendance(@RequestBody EmployeeAttendance employeeAttendance) {
        EmployeeAttendance createdAttendance = employeeAttendanceService.createEmployeeAttendance(employeeAttendance);
        return new ResponseEntity<>(createdAttendance, HttpStatus.CREATED);
    }


}
