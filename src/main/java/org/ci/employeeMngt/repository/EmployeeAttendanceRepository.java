package org.ci.employeeMngt.repository;

import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.entity.EmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance, Long> {

    List<EmployeeAttendance> findByEmployee_EmpId(Long empId);


    // Query to retrieve distinct employee IDs from EmployeeAttendance
    @Query("SELECT DISTINCT ea.employee.empId FROM EmployeeAttendance ea")
    List<Long> findAllEmployeeIds();
}