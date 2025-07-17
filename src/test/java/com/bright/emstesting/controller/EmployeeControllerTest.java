package com.bright.emstesting.controller;

import com.bright.emstesting.dto.request.EmployeeRequestDto;
import com.bright.emstesting.dto.response.EmployeeResponseDto;
import com.bright.emstesting.exception.employee.DuplicateEmailException;
import com.bright.emstesting.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    //to simulate the HTTP request
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeRequestDto employeeRequestDto =
            new EmployeeRequestDto("John", "Doe", "john.doe@gmail.com", "IT");

    private EmployeeResponseDto employeeResponseDto =
            new EmployeeResponseDto("John", "Doe", "IT");

    @Test
    @DisplayName("POST /employees should create and return employee")
    void givenEmployeeRequestDto_whenCreate_thenReturnSavedEmployeeResponseDto() throws Exception {
        //Set the mockito behavior for creating an employee
        Mockito.when(employeeService.createEmployee(employeeRequestDto)).thenReturn(employeeResponseDto);

        //when (send a post request)
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequestDto))
        )
                //then
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(employeeResponseDto)))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("GET /employees should return all employees")
    void getEmployees_shouldReturnAllEmployees() throws Exception {
        EmployeeResponseDto employeeResponseDto2 =
                new EmployeeResponseDto("Jane", "Doe", "IT");
        //Set the mockito behavior for getting all employees
        Mockito.when(employeeService.getAllEmployees()).thenReturn(List.of(employeeResponseDto, employeeResponseDto2));

        //when (send a get request)
        mockMvc.perform(
                //build the GET request
                MockMvcRequestBuilders.get("/api/v1/employees")
        )
                //then
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$[0].firstName").value("John"),
                        MockMvcResultMatchers.jsonPath("$[1].lastName").value("Doe")
                )
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("POST /employees with existing email should return BAD REQUEST")
    void givenEmployeeRequestDtoWithExistingEmail_whenCreate_thenReturnBadRequest() throws Exception {
        //set the mockito behavior for creating an employee
        Mockito.when(employeeService.createEmployee(employeeRequestDto))
                .thenThrow(new DuplicateEmailException(employeeRequestDto.email()));
        //when (send a post request)
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequestDto))
        )
                //then
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }




}