package com.bright.emstesting.repository;

import com.bright.emstesting.exception.employee.DuplicateEmailException;
import com.bright.emstesting.model.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .departmentCode("IT")
                .build();
    }

    @Test
    @DisplayName("Test for creating a new employee")
    void givenNonExistentEmployee_whenSave_thenReturnsResponseDto() {
        //Given
        //Given employee is not in the database
        //When
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        //Then
        //test whether entity is saved or not
        assertNotNull(savedEmployee);//assert that savedEmployee is not null
        assertEquals("John", savedEmployee.getFirstName());
        assertEquals("Doe", savedEmployee.getLastName());
        //...
    }

    @Test
    @DisplayName("Test for saving an existing employee")
    public void givenExistingEmployee_whenSave_thenThrowException() {
        //Given
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        Employee employee2 = Employee.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.doe@gmail.com")
                .build();
        //throw exception when save existing employee
        assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.saveAndFlush(employee2));
    }
    
    @Test
    @DisplayName("Test for finding an employee by email")
    void givenExistingEmail_whenFindByEmail_thenReturnEmployee() {
        //Given
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);

        //When
        Optional<Employee> foundEmployee = employeeRepository.findByEmail("john.doe@gmail.com");

        //Then
        assertTrue(foundEmployee.isPresent());
        assertEquals("John", foundEmployee.get().getFirstName());
        assertEquals("Doe", foundEmployee.get().getLastName());
        assertEquals("john.doe@gmail.com", foundEmployee.get().getEmail());
        assertEquals("IT", foundEmployee.get().getDepartmentCode());
    }
    
    @Test
    @DisplayName("Test for finding employees by department code")
    void givenExistingDepartmentCode_whenFindByDepartmentCode_thenReturnEmployeeList() {
        //Given
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        Employee employee2 = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@gmail.com")
                .departmentCode("IT")
                .build();
        employeeRepository.saveAndFlush(employee2);

        //When
        List<Employee> foundEmployees = employeeRepository.findByDepartmentCodeIgnoreCase("IT");
        //Get all employees' emails
        List<String> foundEmails = foundEmployees.stream().map(Employee::getEmail).toList();
        //Then
        assertNotNull(foundEmployees);
        assertEquals(2, foundEmployees.size());
        assertTrue(foundEmployees.stream().allMatch(emp -> emp.getDepartmentCode().equals("IT")));
        Assertions.assertThat(foundEmails)
                .containsExactlyInAnyOrder("jane.smith@gmail.com", "john.doe@gmail.com");
    }

    @Test
    @DisplayName("Test for deleting an existing employee")
    void givenExistingEmployee_whenDelete_thenDeleted () {
        //Given
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        //When
//        employeeRepository.deleteById(savedEmployee.getId());
//        testEntityManager.flush();
        employeeRepository.deleteByEmail(employee.getEmail());//delete query
        //Then
        //write code to find the deleted employee
//        Optional<Employee> foundEmployee = employeeRepository.findByEmail(employee.getEmail());
        Employee foundEmployee = employeeRepository.findByEmail(employee.getEmail()).orElse(null);
        assertNull(foundEmployee);
    }
}