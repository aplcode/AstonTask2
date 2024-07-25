package ru.artemlychko.rest.service;

import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.servlet.dto.EmployeeInDto;
import ru.artemlychko.rest.servlet.dto.EmployeeOutDto;
import ru.artemlychko.rest.servlet.dto.EmployeeUpdateDto;

import java.util.List;

public interface EmployeeService {
    EmployeeOutDto save(EmployeeInDto employeeInDto);

    void update(EmployeeUpdateDto employeeUpdateDto) throws NotFoundException;

    EmployeeOutDto findById(Long employeeId) throws NotFoundException;

    List<EmployeeOutDto> findAll();

    void delete(Long employeeId) throws NotFoundException;
}
