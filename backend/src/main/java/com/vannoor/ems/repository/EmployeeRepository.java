package com.vannoor.ems.repository;

import com.vannoor.ems.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA generates the implementation at startup.
 * JpaRepository already gives save, findById, findAll, delete and more.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Derived queries: Spring builds the SQL from the method name.
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    // Custom JPQL query used by the search box in the UI.
    @Query("""
            SELECT e FROM Employee e
            WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY e.id
            """)
    List<Employee> search(@Param("keyword") String keyword);
}
