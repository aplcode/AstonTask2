package ru.artemlychko.rest.servlet.mapper.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.servlet.dto.DepartmentInDto;
import ru.artemlychko.rest.servlet.dto.DepartmentOutDto;
import ru.artemlychko.rest.servlet.dto.DepartmentUpdateDto;
import ru.artemlychko.rest.servlet.mapper.DepartmentDtoMapper;

import java.util.List;

class DepartmentDtoMapperImplTest {
    private DepartmentDtoMapper departmentDtoMapper;

    @BeforeEach
    void setUp() {
        departmentDtoMapper = DepartmentDtoMapperImpl.getInstance();
    }

    @DisplayName("Department map(DepartmentInDto)")
    @Test
    void mapIn() {
        DepartmentInDto departmentInDto = new DepartmentInDto("Department1");

        Department result = departmentDtoMapper.map(departmentInDto);

        Assertions.assertNull(result.getId());
        Assertions.assertEquals(departmentInDto.getName(), result.getName());
    }

    @DisplayName("Department map(DepartmentUpdateDto)")
    @Test
    void mapUpdate() {
        DepartmentUpdateDto departmentUpdateDto = new DepartmentUpdateDto(
                1L,
                "Department2"
        );

        Department result = departmentDtoMapper.map(departmentUpdateDto);

        Assertions.assertEquals(departmentUpdateDto.getId(), result.getId());
        Assertions.assertEquals(departmentUpdateDto.getName(), result.getName());
    }

    @DisplayName("DepartmentOutDto map(Department)")
    @Test
    void mapOut() {
        Department department = new Department(
                1L,
                "Department1",
                List.of()
        );

        DepartmentOutDto result = departmentDtoMapper.map(department);

        Assertions.assertEquals(department.getId(), result.getId());
        Assertions.assertEquals(department.getName(), result.getName());
        Assertions.assertEquals(department.getEmployeeList().size(), result.getEmployeeList().size());
    }

    @DisplayName("List<DepartmentOutDto> map(List<Department>")
    @Test
    void mapList() {
        List<Department> departments = List.of(
                new Department(
                        1L,
                        "Department1",
                        List.of(
                                new Employee(),
                                new Employee(),
                                new Employee()
                        )
                ),
                new Department(
                        2L,
                        "Department2",
                        List.of(
                                new Employee(),
                                new Employee(),
                                new Employee()
                        )
                )
        );

        List<DepartmentOutDto> result = departmentDtoMapper.map(departments);

        Assertions.assertEquals(departments.size(), result.size());
    }

    @DisplayName("List<Department> mapUpdateList(List<DepartmentUpdateDto>)")
    @Test
    void mapUpdateList() {
        List<DepartmentUpdateDto> departmentUpdateDtoList = List.of(
                new DepartmentUpdateDto(
                        1L,
                        "Department1"
                ),
                new DepartmentUpdateDto(
                        2L,
                        "Department2"
                )
        );

        List<Department> result = departmentDtoMapper.mapUpdateList(departmentUpdateDtoList);

        Assertions.assertEquals(departmentUpdateDtoList.size(), result.size());
    }
}
