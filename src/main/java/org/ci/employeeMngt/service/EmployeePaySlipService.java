package org.ci.employeeMngt.service;


import org.ci.employeeMngt.entity.EmployeeAttendance;
import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.entity.EmployeePaySlip;
import org.ci.employeeMngt.repository.EmployeeAttendanceRepository;
import org.ci.employeeMngt.repository.EmployeePaySlipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class EmployeePaySlipService {

    @Autowired
    private EmployeeAttendanceRepository attendanceRepository;


    @Autowired
    private EmployeePaySlipRepository paySlipRepository;

    @Async
    @Transactional
    public CompletableFuture<String> generatePaySlip(Long empId) {
        try {
            long EMPLOYEE_DAILY_SALARY = 1000;

            // Fetch employee attendance records
            List<EmployeeAttendance> attendanceList = attendanceRepository.findByEmployeeEmpId(empId);

            // Calculate total present days
            int totalPresentDays = 0;
            for (EmployeeAttendance attendance : attendanceList) {
                if ("Present".equalsIgnoreCase(attendance.getEaStatus())) {
                    totalPresentDays++;
                }
            }

            // Calculate total salary
            double totalSalary = totalPresentDays * EMPLOYEE_DAILY_SALARY;

            // Save the paySlip record
            EmployeePaySlip paySlip = new EmployeePaySlip();
            paySlip.setEpDaysPresent(totalPresentDays);
            paySlip.setEpDailySalary(EMPLOYEE_DAILY_SALARY);
            paySlip.setEpTotalSalary(totalSalary);
            paySlip.setCreatedAt(LocalDate.now());

            paySlipRepository.save(paySlip);

            // Return a CompletableFuture with the pay slip information as a String
            return CompletableFuture.completedFuture("Pay slip generated successfully for empId: " + empId);
        } catch (Exception e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

}

