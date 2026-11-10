package com.vannoor.ems.service;

import com.vannoor.ems.dto.EmployeeRequest;
import com.vannoor.ems.dto.EmployeeResponse;
import com.vannoor.ems.exception.DuplicateEmailException;
import com.vannoor.ems.exception.ResourceNotFoundException;
import com.vannoor.ems.model.Employee;
import com.vannoor.ems.repository.EmployeeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic lives here: duplicate-email check, "not found" handling and
 * converting between entities and DTOs. The controller stays thin.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    // Constructor injection: Spring passes in the repository bean.
    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll(String search) {
        List<Employee> employees = (search == null || search.isBlank())
                ? repository.findAll(Sort.by("id"))
                : repository.search(search.trim());
        return employees.stream().map(EmployeeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return EmployeeResponse.from(findOrThrow(id));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        String email = normalizeEmail(request.email());
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(email);
        }
        Employee employee = new Employee();
        copyFields(employee, request, email);
        return EmployeeResponse.from(repository.save(employee));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findOrThrow(id);
        String email = normalizeEmail(request.email());
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateEmailException(email);
        }
        copyFields(employee, request, email);
        return EmployeeResponse.from(repository.save(employee));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private Employee findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private void copyFields(Employee employee, EmployeeRequest request, String email) {
        employee.setFirstName(request.firstName().trim());
        employee.setLastName(request.lastName().trim());
        employee.setEmail(email);
        employee.setDepartment(request.department().trim());
        employee.setJobTitle(request.jobTitle().trim());
        employee.setSalary(request.salary());
        employee.setJoiningDate(request.joiningDate());
    }
}
