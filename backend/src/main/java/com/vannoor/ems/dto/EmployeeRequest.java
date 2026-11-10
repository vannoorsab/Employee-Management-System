package com.vannoor.ems.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data the client sends when creating or updating an employee.
 * The annotations below are the validation rules. They are checked when the
 * controller method parameter is annotated with @Valid.
 */
public record EmployeeRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must be at most 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must be at most 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        @Size(max = 100, message = "Email must be at most 100 characters")
        String email,

        @NotBlank(message = "Department is required")
        @Size(max = 50, message = "Department must be at most 50 characters")
        String department,

        @NotBlank(message = "Job title is required")
        @Size(max = 80, message = "Job title must be at most 80 characters")
        String jobTitle,

        @NotNull(message = "Salary is required")
        @Positive(message = "Salary must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Salary can have at most 10 digits and 2 decimal places")
        BigDecimal salary,

        @NotNull(message = "Joining date is required")
        @PastOrPresent(message = "Joining date cannot be in the future")
        LocalDate joiningDate
) {
}
