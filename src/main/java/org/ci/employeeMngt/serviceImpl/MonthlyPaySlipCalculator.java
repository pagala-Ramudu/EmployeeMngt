package org.ci.employeeMngt.serviceImpl;

import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.entity.EmployeeAttendance;
import org.ci.employeeMngt.entity.EmployeePaySlip;
import org.ci.employeeMngt.exception1.APIErrorCode;
import org.ci.employeeMngt.exception1.InspireException;
import org.ci.employeeMngt.repository.EmployeeAttendanceRepository;
import org.ci.employeeMngt.repository.EmployeePaySlipRepository;
import org.ci.employeeMngt.repository.EmployeeRepository;
import org.ci.employeeMngt.service.PaySlipCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class MonthlyPaySlipCalculator implements PaySlipCalculator {
    private static final double EMPLOYEE_MONTHLY_SALARY = 30000;


    @Autowired
    EmployeePaySlipRepository employeePaySlipRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeAttendanceRepository attendanceRepository;

    @Override
    public String calculatePaySlip(Long empId) {
        try {
            // Check if a paySlip already exists for the employee
            if (employeePaySlipRepository.existsByEmployeeEmpId(empId)) {
                throw new InspireException(APIErrorCode.RESOURCE_NOT_FOUND,"Payslip already present for the employee "+empId);
            }

            // Fetch employee attendance records
            List<EmployeeAttendance> attendanceList = attendanceRepository.findByEmployee_EmpId(empId);

            if (attendanceList.isEmpty()) {
                throw new InspireException(APIErrorCode.RESOURCE_NOT_FOUND);
            }

            // Calculate total present days for the month
            long totalPresentDays = attendanceList.stream()
                    .filter(attendance -> "Present".equalsIgnoreCase(attendance.getEaStatus()))
                    .count();

            // Calculate total salary for the month
            double totalSalary = totalPresentDays * (EMPLOYEE_MONTHLY_SALARY / getTotalDaysInMonth());

            // Fetch the employee
            Employee employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new InspireException(APIErrorCode.RESOURCE_NOT_FOUND));

            // Save the paySlip record
            EmployeePaySlip paySlip = new EmployeePaySlip();
            paySlip.setEmployee(employee);
            paySlip.setEpDaysPresent((int) totalPresentDays);
            paySlip.setEpDailySalary(EMPLOYEE_MONTHLY_SALARY / getTotalDaysInMonth());
            paySlip.setEpTotalSalary(totalSalary);
            paySlip.setCreatedAt(LocalDate.now());
            employeePaySlipRepository.save(paySlip);

            // Return a message indicating successful payslip generation
            return "Monthly Pay slip generated successfully for empId: " + empId;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getTotalDaysInMonth() {
        LocalDate currentDate = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(currentDate);
        return currentMonth.lengthOfMonth();
    }
}
