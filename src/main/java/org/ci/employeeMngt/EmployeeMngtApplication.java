package org.ci.employeeMngt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EmployeeMngtApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeMngtApplication.class, args);
	}

}
