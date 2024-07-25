package ru.artemlychko.rest.servlet.mapper.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.servlet.dto.ProjectInDto;
import ru.artemlychko.rest.servlet.dto.ProjectOutDto;
import ru.artemlychko.rest.servlet.dto.ProjectUpdateDto;
import ru.artemlychko.rest.servlet.mapper.ProjectDtoMapper;

import java.util.List;

class ProjectDtoMapperImplTest {
    private ProjectDtoMapper projectDtoMapper;

    @BeforeEach
    void setUp() {
        projectDtoMapper = ProjectDtoMapperImpl.getInstance();
    }

    @DisplayName("Project map(ProjectInDto)")
    @Test
    void mapIn() {
        ProjectInDto projectInDto = new ProjectInDto("Project1");
        Project result = projectDtoMapper.map(projectInDto);

        Assertions.assertNull(result.getId());
        Assertions.assertEquals(projectInDto.getName(), result.getName());
    }

    @DisplayName("ProjectInDto map(project)")
    @Test
    void mapOut() {
        Project project = new Project(
                1L,
                "Project1",
                List.of(new Employee(), new Employee())
        );

        ProjectOutDto result = projectDtoMapper.map(project);

        Assertions.assertEquals(project.getId(), result.getId());
        Assertions.assertEquals(project.getName(), result.getName());
        Assertions.assertEquals(project.getEmployeeList().size(), result.getEmployeeList().size());
    }

    @DisplayName("Project map(ProjectUpdateDto)")
    @Test
    void mapUpdate() {
        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto(
                1L,
                "ProjectUpdate1"
        );

        Project result = projectDtoMapper.map(projectUpdateDto);
        Assertions.assertEquals(projectUpdateDto.getId(), result.getId());
        Assertions.assertEquals(projectUpdateDto.getName(), result.getName());
    }

    @DisplayName("List<ProjectUpdateDto> map(List<Project>)")
    @Test
    void mapList() {
        List<Project> projectList = List.of(
                new Project(1L, "Project1", List.of()),
                new Project(2L, "Project2", List.of()),
                new Project(3L, "Project3", List.of())
        );

        List<ProjectOutDto> result = projectDtoMapper.map(projectList);
        Assertions.assertEquals(projectList.size(), result.size());
    }

    @DisplayName("List<Project> mapUpdateList(List<ProjectUpdateDto)")
    @Test
    void mapUpdateList() {
        List<ProjectUpdateDto> projectUpdateDtoList = List.of(
                new ProjectUpdateDto(),
                new ProjectUpdateDto(),
                new ProjectUpdateDto()
        );

        List<Project> result = projectDtoMapper.mapUpdateList(projectUpdateDtoList);

        Assertions.assertEquals(projectUpdateDtoList.size(), result.size());
    }
}
