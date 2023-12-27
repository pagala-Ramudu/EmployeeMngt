package org.ci.employeeMngt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "EMPLOYEE")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMP_ID")
    private Long empId;

    @Column(name = "EMP_NAME")
    private String empName;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "employee")
    private List<EmployeeAttendance> employeeAttendances = new ArrayList<>();



    public void addEmployeeAttendance(EmployeeAttendance employeeAttendance) {
        employeeAttendances.add(employeeAttendance);
        employeeAttendance.setEmployee(this);
    }

    public void removeEmployeeAttendance(EmployeeAttendance employeeAttendance) {
        employeeAttendances.remove(employeeAttendance);
        employeeAttendance.setEmployee(null);

    }

}
