package org.ci.employeeMngt.service;

import org.ci.employeeMngt.entity.EmployeeAttendance;
import org.ci.employeeMngt.repository.EmployeeAttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeAttendanceService {
    @Autowired
    private EmployeeAttendanceRepository employeeAttendanceRepository;

    public EmployeeAttendance createEmployeeAttendance(EmployeeAttendance employeeAttendance) {

        return employeeAttendanceRepository.save(employeeAttendance);
    }


}

