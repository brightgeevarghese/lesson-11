package com.bright.emstesting.service.impl;

import com.bright.emstesting.dto.request.EmployeeRequestDto;
import com.bright.emstesting.dto.response.EmployeeResponseDto;
import com.bright.emstesting.exception.employee.DuplicateEmailException;
import com.bright.emstesting.model.Employee;
import com.bright.emstesting.repository.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    //Mock employeeRepository
    @Mock
    private EmployeeRepository employeeRepository;

    //Inject employeeRepository to EmployeeServiceImpl
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequestDto employeeRequestDto;

    @BeforeEach
    void setUp() {
        //an employee
        employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .departmentCode("IT")
                .build();
        //an employeeRequestDto
        employeeRequestDto =
                new EmployeeRequestDto("John", "Doe", "john.doe@gmail.com", "IT");
    }

    @Test
    @DisplayName("Create employee when email does not exist")
    void givenEmployeeRequestDto_whenCreate_thenReturnEmployeeResponseDto () {
        //Set the Mockito behavior
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.empty());
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);

        //when
        EmployeeResponseDto employeeResponseDto = employeeService.createEmployee(employeeRequestDto);

        //then
        Assertions.assertThat(employeeResponseDto)
                .isEqualTo(
                        new EmployeeResponseDto(
                                employeeRequestDto.firstName(),
                                employeeRequestDto.lastName(),
                                employeeRequestDto.departmentCode()
                        )
                );
        Mockito.verify(employeeRepository, Mockito.times(1))
                .findByEmail(employeeRequestDto.email());
        Mockito.verify(employeeRepository, Mockito.times(1))
                .save(Mockito.any(Employee.class));
    }

    @Test
    @DisplayName("Create employee with existing email should throw Exception")
    void givenEmployeeWithExistingEmail_whenCreate_thenThrowsDuplicateEmailException() {
        //Set the Mockito behavior
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.of(employee));
        assertThrows(DuplicateEmailException.class, () -> employeeService.createEmployee(employeeRequestDto));
        Mockito.verify(employeeRepository, Mockito.times(1))
                .findByEmail(employeeRequestDto.email());
        Mockito.verify(employeeRepository, Mockito.never())
                .save(Mockito.any(Employee.class));
    }

    @Test
    @DisplayName("Update employee when email exists should return updated response dto")
    void givenExistingEmployee_whenUpdate_thenReturnUpdatedResponseDto() {
        //Set the Mockito behavior
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);
        //when
        EmployeeResponseDto employeeResponseDto =
                employeeService.updateEmployee(employeeRequestDto.email(), employeeRequestDto);
        //then
        Mockito.verify(employeeRepository, Mockito.times(1))
                .findByEmail(employeeRequestDto.email());
        Mockito.verify(employeeRepository, Mockito.times(1))
                .save(Mockito.any(Employee.class));
        Assertions.assertThat(employeeResponseDto).isEqualTo(new EmployeeResponseDto(
                employeeRequestDto.firstName(),
                employeeRequestDto.lastName(),
                employeeRequestDto.departmentCode()
        ));
    }

    @Test
    @DisplayName("Delete an existing employee")
    void givenExistingEmail_whenDelete_thenDeleted(){
        //Set the Mockito behavior
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.of(employee));
        Mockito.doNothing().when(employeeRepository).deleteByEmail(employeeRequestDto.email());
        //when
        employeeService.deleteEmployee(employeeRequestDto.email());
        //then
        Mockito.verify(employeeRepository, Mockito.times(1))
                .findByEmail(employeeRequestDto.email());
        Mockito.verify(employeeRepository, Mockito.times(1))
                .deleteByEmail(employeeRequestDto.email());
    }
}







