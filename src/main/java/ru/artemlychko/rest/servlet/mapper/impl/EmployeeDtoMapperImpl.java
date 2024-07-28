package ru.artemlychko.rest.servlet.mapper.impl;

import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.servlet.dto.EmployeeInDto;
import ru.artemlychko.rest.servlet.dto.EmployeeOutDto;
import ru.artemlychko.rest.servlet.dto.EmployeeUpdateDto;
import ru.artemlychko.rest.servlet.mapper.DepartmentDtoMapper;
import ru.artemlychko.rest.servlet.mapper.EmployeeDtoMapper;
import ru.artemlychko.rest.servlet.mapper.ProjectDtoMapper;

import java.util.List;

public class EmployeeDtoMapperImpl implements EmployeeDtoMapper {
    private static final ProjectDtoMapper projectDtoMapper = ProjectDtoMapperImpl.getInstance();
    private static final DepartmentDtoMapper departmentDtoMapper = DepartmentDtoMapperImpl.getInstance();

    private static EmployeeDtoMapper instance;

    private EmployeeDtoMapperImpl() {
    }

    public static synchronized EmployeeDtoMapper getInstance() {
        if (instance == null) {
            instance = new EmployeeDtoMapperImpl();
        }
        return instance;
    }


    @Override
    public Employee map(EmployeeInDto employeeInDto) {
        return new Employee(
                null,
                employeeInDto.getFirstName(),
                employeeInDto.getLastName(),
                employeeInDto.getDepartment(),
                null
        );
    }

    @Override
    public Employee map(EmployeeUpdateDto employeeUpdateDto) {
        return new Employee(
                employeeUpdateDto.getId(),
                employeeUpdateDto.getFirstName(),
                employeeUpdateDto.getLastName(),
                departmentDtoMapper.map(employeeUpdateDto.getDepartment()),
                projectDtoMapper.mapUpdateList(employeeUpdateDto.getProjectList())
        );
    }

    @Override
    public EmployeeOutDto map(Employee employee) {
        return new EmployeeOutDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                departmentDtoMapper.mapForEmployee(employee.getDepartment()),
                projectDtoMapper.mapForEmployee(employee.getProjectList())
        );
    }

    @Override
    public List<EmployeeOutDto> map(List<Employee> employeeList) {
        return employeeList.stream().map(this::map).toList();
    }
}
