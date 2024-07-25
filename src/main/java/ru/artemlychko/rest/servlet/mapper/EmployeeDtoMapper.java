package ru.artemlychko.rest.servlet.mapper;

import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.servlet.dto.EmployeeInDto;
import ru.artemlychko.rest.servlet.dto.EmployeeOutDto;
import ru.artemlychko.rest.servlet.dto.EmployeeUpdateDto;

import java.util.List;

public interface EmployeeDtoMapper {
    Employee map(EmployeeInDto employeeInDto);

    Employee map(EmployeeUpdateDto employeeUpdateDto);

    EmployeeOutDto map(Employee employee);

    List<EmployeeOutDto> map(List<Employee> employees);
}
