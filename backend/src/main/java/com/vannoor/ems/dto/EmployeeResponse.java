package com.vannoor.ems.dto;

import com.vannoor.ems.model.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data the API sends back to the client. Keeping this separate from the
 * Employee entity means the database model is not exposed directly.
 */
public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String department,
        String jobTitle,
        BigDecimal salary,
        LocalDate joiningDate
) {

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getJobTitle(),
                employee.getSalary(),
                employee.getJoiningDate()
        );
    }
}
