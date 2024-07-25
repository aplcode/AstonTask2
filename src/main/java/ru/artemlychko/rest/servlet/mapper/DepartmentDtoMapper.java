package ru.artemlychko.rest.servlet.mapper;

import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.servlet.dto.DepartmentInDto;
import ru.artemlychko.rest.servlet.dto.DepartmentOutDto;
import ru.artemlychko.rest.servlet.dto.DepartmentUpdateDto;

import java.util.List;

public interface DepartmentDtoMapper {
    Department map (DepartmentInDto departmentInDto);

    Department map(DepartmentUpdateDto departmentUpdateDto);

    DepartmentOutDto map(Department department);

    List<DepartmentOutDto> map(List<Department> departments);

    List<Department> mapUpdateList(List<DepartmentUpdateDto> departmentList);
}
