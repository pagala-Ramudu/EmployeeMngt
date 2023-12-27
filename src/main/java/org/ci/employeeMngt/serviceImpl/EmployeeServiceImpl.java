package org.ci.employeeMngt.serviceImpl;

import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.entity.EmployeeAttendance;
import org.ci.employeeMngt.entity.EmployeePaySlip;
import org.ci.employeeMngt.repository.EmployeeAttendanceRepository;
import org.ci.employeeMngt.repository.EmployeePaySlipRepository;
import org.ci.employeeMngt.repository.EmployeeRepository;
import org.ci.employeeMngt.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeAttendanceRepository employeeAttendanceRepository;

    @Autowired
    private EmployeeAttendanceRepository attendanceRepository;


    @Autowired
    private EmployeePaySlipRepository paySlipRepository;

    //save employee method
    @Override
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    //method to get employee by Id
    @Override
    public Employee getEmployeeById(Long empId) {
        return employeeRepository.findById(empId).orElse(null);
    }


    //remove employee attendance
    @Override
    public void removeEmployeeAttendance(Long empId, Long attendanceId) {
        Employee employee = getEmployeeById(empId);
        if (employee != null) {
            employee.getEmployeeAttendances().removeIf(attendance -> attendance.getEaId().equals(attendanceId));
        }
    }

    //generate payslip to employee
    @Override
    @Async
    @Transactional
    public CompletableFuture<String> generatePaySlip(Long empId) {
        try {
            long EMPLOYEE_DAILY_SALARY = 1000;

            // Check if a pay slip already exists for the employee
            if (paySlipRepository.existsByEmployeeEmpId(empId)) {
                throw new RuntimeException("Pay slip already generated for empId: " + empId);
            }

            // Fetch employee attendance records
            List<EmployeeAttendance> attendanceList = attendanceRepository.findByEmployee_EmpId(empId);


            if (attendanceList.isEmpty()) {
                throw new RuntimeException("Employee with ID " + empId + " not found");
            }

            // Calculate total present days
            int totalPresentDays = 0;
            for (EmployeeAttendance attendance : attendanceList) {
                if ("Present".equalsIgnoreCase(attendance.getEaStatus())) {
                    totalPresentDays++;
                }
            }

            // Calculate total salary
            double totalSalary = totalPresentDays * EMPLOYEE_DAILY_SALARY;

            // Fetch the employee
            Employee employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new RuntimeException(""));

            // Save the paySlip record
            EmployeePaySlip paySlip = new EmployeePaySlip();
            paySlip.setEmployee(employee);
            paySlip.setEpDaysPresent(totalPresentDays);
            paySlip.setEpDailySalary(EMPLOYEE_DAILY_SALARY);
            paySlip.setEpTotalSalary(totalSalary);
            paySlip.setCreatedAt(LocalDate.now());


            paySlipRepository.save(paySlip);

            // Return a CompletableFuture with the payslip information as a String
            return CompletableFuture.completedFuture("Pay slip generated successfully for empId: " + empId);
        } catch (Exception e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    //save employee with attandance
    public Employee saveEmployeeWithAttendances(Employee employee) {
        for (EmployeeAttendance attendance : employee.getEmployeeAttendances()) {
            attendance.setEmployee(employee);
        }
        return employeeRepository.save(employee);
    }

    //add employeeAttendance
    public EmployeeAttendance addEmployeeAttendance(Long empId, EmployeeAttendance employeeAttendance) {
        Employee employee = getEmployeeById(empId);
        if (employee != null) {
            employeeAttendance.setEmployee(employee);
            return employeeAttendanceRepository.save(employeeAttendance);
        }
        return null;
    }

    //scheduler to generate payslip
    @Scheduled(cron = "0 10 16 * * ?")
    public void generatePaySlips() {

        // Fetch all employee IDs
        List<Long> employeeIds = employeeAttendanceRepository.findAllEmployeeIds();

        // Generate payslip for each employee
        for (Long empId : employeeIds) {
          generatePaySlip(empId);
        }
    }

}






