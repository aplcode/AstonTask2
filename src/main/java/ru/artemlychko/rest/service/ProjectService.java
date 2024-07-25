package ru.artemlychko.rest.service;

import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.servlet.dto.ProjectInDto;
import ru.artemlychko.rest.servlet.dto.ProjectOutDto;
import ru.artemlychko.rest.servlet.dto.ProjectUpdateDto;

import java.util.List;

public interface ProjectService {
    ProjectOutDto save(ProjectInDto projectInDto);

    void update(ProjectUpdateDto projectUpdateDto) throws NotFoundException;

    ProjectOutDto findById(Long projectId) throws NotFoundException;

    List<ProjectOutDto> findAll();

    void delete(Long projectId) throws NotFoundException;

    void deleteEmployeeFromProject(Long projectId, Long employeeId) throws NotFoundException;

    void addEmployeeToProject(Long projectId, Long employeeId) throws NotFoundException;
}
