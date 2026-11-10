package com.vannoor.ems.controller;

import com.vannoor.ems.dto.EmployeeRequest;
import com.vannoor.ems.dto.EmployeeResponse;
import com.vannoor.ems.exception.DuplicateEmailException;
import com.vannoor.ems.exception.ResourceNotFoundException;
import com.vannoor.ems.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the web layer only (controller + validation + exception handler).
 * The service is mocked, so no database is needed.
 */
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    private static final String VALID_JSON = """
            {
              "firstName": "Asha",
              "lastName": "Rao",
              "email": "asha.rao@example.com",
              "department": "Engineering",
              "jobTitle": "Software Engineer",
              "salary": 65000.00,
              "joiningDate": "2024-06-01"
            }
            """;

    private static final String INVALID_JSON = """
            {
              "firstName": "",
              "lastName": "Rao",
              "email": "not-an-email",
              "department": "Engineering",
              "jobTitle": "Software Engineer",
              "salary": -5,
              "joiningDate": "2999-01-01"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @Test
    void createEmployee_withValidData_returns201AndLocation() throws Exception {
        EmployeeResponse saved = new EmployeeResponse(1L, "Asha", "Rao", "asha.rao@example.com",
                "Engineering", "Software Engineer", new BigDecimal("65000.00"), LocalDate.of(2024, 6, 1));
        when(service.create(any(EmployeeRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/employees/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("asha.rao@example.com"));
    }

    @Test
    void createEmployee_withInvalidData_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.firstName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.salary").exists())
                .andExpect(jsonPath("$.fieldErrors.joiningDate").exists());

        verifyNoInteractions(service);
    }

    @Test
    void createEmployee_withDuplicateEmail_returns409() throws Exception {
        when(service.create(any(EmployeeRequest.class)))
                .thenThrow(new DuplicateEmailException("asha.rao@example.com"));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void getEmployee_whenMissing_returns404() throws Exception {
        when(service.getById(99L)).thenThrow(new ResourceNotFoundException("Employee not found with id 99"));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id 99"));
    }

    @Test
    void getEmployee_withNonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/api/employees/abc"))
                .andExpect(status().isBadRequest());
    }
}
