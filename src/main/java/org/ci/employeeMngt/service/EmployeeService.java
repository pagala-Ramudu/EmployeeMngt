package org.ci.employeeMngt.service;

import org.ci.employeeMngt.entity.Employee;

import java.util.concurrent.CompletableFuture;

public interface EmployeeService {

    public void removeEmployeeAttendance(Long empId, Long attendanceId);

    public Employee createEmployee(Employee employee);

    public Employee getEmployeeById(Long empId);

    public CompletableFuture<String> generatePaySlip(Long empId);
}
