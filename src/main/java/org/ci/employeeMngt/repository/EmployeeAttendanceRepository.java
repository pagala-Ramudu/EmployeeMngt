package org.ci.employeeMngt.repository;

import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.entity.EmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance, Long> {


    List<EmployeeAttendance> findByEmployeeEmpId(Long empId);
}