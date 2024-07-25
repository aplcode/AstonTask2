package ru.artemlychko.rest.servlet.mapper.impl;

import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.servlet.dto.EmployeeShortOutDto;
import ru.artemlychko.rest.servlet.dto.ProjectInDto;
import ru.artemlychko.rest.servlet.dto.ProjectOutDto;
import ru.artemlychko.rest.servlet.dto.ProjectUpdateDto;
import ru.artemlychko.rest.servlet.mapper.ProjectDtoMapper;

import java.util.List;

public class ProjectDtoMapperImpl implements ProjectDtoMapper {
    private static ProjectDtoMapper instance;

    public ProjectDtoMapperImpl() {
    }

    public static synchronized ProjectDtoMapper getInstance() {
        if (instance == null) {
            instance = new ProjectDtoMapperImpl();
        }
        return instance;
    }


    @Override
    public Project map(ProjectInDto projectInDto) {
        return new Project(
                null,
                projectInDto.getName(),
                null
        );
    }

    @Override
    public ProjectOutDto map(Project project) {
        List<EmployeeShortOutDto> employeeList = project.getEmployeeList()
                .stream().map(employee -> new EmployeeShortOutDto(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName()
                )).toList();
        return new ProjectOutDto(
                project.getId(),
                project.getName(),
                employeeList
        );
    }

    @Override
    public Project map(ProjectUpdateDto projectUpdateDto) {
        return new Project(
                projectUpdateDto.getId(),
                projectUpdateDto.getName(),
                null
        );
    }

    @Override
    public List<ProjectOutDto> map(List<Project> projectList) {
        return projectList.stream().map(this::map).toList();
    }

    @Override
    public List<Project> mapUpdateList(List<ProjectUpdateDto> projectList) {
        return projectList.stream().map(this::map).toList();
    }
}
