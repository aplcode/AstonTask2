package ru.artemlychko.rest.servlet.mapper.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.servlet.dto.*;
import ru.artemlychko.rest.servlet.mapper.EmployeeDtoMapper;

import java.util.List;

class EmployeeDtoMapperImplTest {
    private EmployeeDtoMapper employeeDtoMapper;

    @BeforeEach
    void setUp() {
        employeeDtoMapper = EmployeeDtoMapperImpl.getInstance();
    }

    @DisplayName("Employee map(EmployeeInDto)")
    @Test
    void mapIn(){
        EmployeeInDto employeeInDto = new EmployeeInDto(
                "fn1",
                "ln1",
                new Department(1L, "departmen1", null));
        Employee result = employeeDtoMapper.map(employeeInDto);
        Assertions.assertNull(result.getId());
        Assertions.assertEquals(employeeInDto.getFirstName(), result.getFirstName());
        Assertions.assertEquals(employeeInDto.getLastName(), result.getLastName());
        Assertions.assertEquals(employeeInDto.getDepartment().getId(), result.getDepartment().getId());
    }

    @DisplayName("Employee map(EmployeeUpdateDto)")
    @Test
    void mapUpdate(){
        EmployeeUpdateDto employeeUpdateDto = new EmployeeUpdateDto(
                1L,
                "fn1",
                "ln1",
                new DepartmentUpdateDto(2L, "Department update"),
                List.of(new ProjectUpdateDto())
        );
        Employee result = employeeDtoMapper.map(employeeUpdateDto);

        Assertions.assertEquals(employeeUpdateDto.getId(), result.getId());
        Assertions.assertEquals(employeeUpdateDto.getFirstName(), result.getFirstName());
        Assertions.assertEquals(employeeUpdateDto.getLastName(), result.getLastName());
        Assertions.assertEquals(employeeUpdateDto.getDepartment().getId(), result.getDepartment().getId());
        Assertions.assertEquals(employeeUpdateDto.getProjectList().size(), result.getProjectList().size());
    }

    @DisplayName("EmployeeOutDto map(Employee)")
    @Test
    void mapOut(){
        Employee employee = new Employee(
                1L,
                "fn1",
                "ln1",
                new Department(1L, "Department1", List.of()),
                List.of(new Project(1L, "Project1", List.of()))
        );

        EmployeeOutDto result = employeeDtoMapper.map(employee);

        Assertions.assertEquals(employee.getId(), result.getId());
        Assertions.assertEquals(employee.getFirstName(), result.getFirstName());
        Assertions.assertEquals(employee.getLastName(), result.getLastName());
        Assertions.assertEquals(employee.getDepartment().getId(), result.getDepartment().getId());
        Assertions.assertEquals(employee.getProjectList().size(), result.getProjectList().size());
    }

    @DisplayName("List<EmployeeOutDto> map(List<Employee)")
    @Test
    void mapList(){
        List<Employee> employeeList = List.of(
                new Employee(
                        1L,
                        "fn1",
                        "ln1",
                        new Department(1L, "Department1", List.of()),
                        List.of(new Project(1L, "Project1", List.of()))
                ),
                new Employee(
                        2L,
                        "fn2",
                        "ln2",
                        new Department(2L, "Department2", List.of()),
                        List.of(new Project(2L, "Project2", List.of()))
                )
        );
        List<EmployeeOutDto> result = employeeDtoMapper.map(employeeList);
        Assertions.assertEquals(employeeList.size(), result.size());
    }


}
