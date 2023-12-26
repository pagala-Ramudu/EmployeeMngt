package org.ci.employeeMngt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EMPLOYEE_ATTANDENCE_TABLE")
public class  EmployeeAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EA_ID")
    private Long eaId;

    @Column(name = "EA_ATTANDENCE_DATE")
    private LocalDate eaAttendanceDate;

    @Column(name = "EA_STATUS")
    private String eaStatus;

    @Column(name = "EA_MONTH")
    private String eaMonth;


    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "EMP_ID")
    private Employee employee;

}
