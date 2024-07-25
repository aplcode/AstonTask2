package ru.artemlychko.rest.service;

import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.servlet.dto.DepartmentInDto;
import ru.artemlychko.rest.servlet.dto.DepartmentOutDto;
import ru.artemlychko.rest.servlet.dto.DepartmentUpdateDto;

import java.util.List;

public interface DepartmentService {
    DepartmentOutDto save(DepartmentInDto departmentInDto);

    void update(DepartmentUpdateDto departmentUpdateDto) throws NotFoundException;

    DepartmentOutDto findById(Long departmentId) throws NotFoundException;

    List<DepartmentOutDto> findAll();

    void delete(Long departmentId) throws NotFoundException;

//    void deleteEmployeeFromDepartment(Long departmentId, Long employeeId) throws NotFoundException;
//
//    void addEmployeeToDepartment(Long departmentId, Long employeeId) throws NotFoundException;
}
