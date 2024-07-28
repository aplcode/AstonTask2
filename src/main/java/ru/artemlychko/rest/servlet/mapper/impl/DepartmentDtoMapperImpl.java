package ru.artemlychko.rest.servlet.mapper.impl;

import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.servlet.dto.*;
import ru.artemlychko.rest.servlet.mapper.DepartmentDtoMapper;

import java.util.List;

public class DepartmentDtoMapperImpl implements DepartmentDtoMapper {
    private static DepartmentDtoMapper instance;

    private DepartmentDtoMapperImpl() {
    }

    public static synchronized DepartmentDtoMapper getInstance() {
        if (instance == null) {
            instance = new DepartmentDtoMapperImpl();
        }
        return instance;
    }

    public Department map(DepartmentInDto departmentInDto) {
        return new Department(
                null,
                departmentInDto.getName(),
                null
        );
    }

    public Department map(DepartmentUpdateDto departmentUpdateDto){
        return new Department(
                departmentUpdateDto.getId(),
                departmentUpdateDto.getName(),
                null
        );
    }

    public DepartmentOutDto map(Department department) {
        List<EmployeeShortOutDto> employeeList = department.getEmployeeList()
                .stream().map(employee -> new EmployeeShortOutDto(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName()
                )).toList();
        return new DepartmentOutDto(
                department.getId(),
                department.getName(),
                employeeList

        );
    }

    @Override
    public DepartmentShortOutDto mapForEmployee(Department department) {
        return new DepartmentShortOutDto(
                department.getId(),
                department.getName()
        );
    }

    @Override
    public List<DepartmentOutDto> map(List<Department> departmentList) {
        return departmentList.stream().map(this::map).toList();
    }

    @Override
    public List<Department> mapUpdateList(List<DepartmentUpdateDto> departmentList) {
        return departmentList.stream().map(this::map).toList();
    }
}
