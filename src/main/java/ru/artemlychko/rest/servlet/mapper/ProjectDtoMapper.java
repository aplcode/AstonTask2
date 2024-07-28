package ru.artemlychko.rest.servlet.mapper;

import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.servlet.dto.*;

import java.util.List;

public interface ProjectDtoMapper {
    Project map(ProjectInDto projectInDto);

    ProjectOutDto map(Project project);

    Project map(ProjectUpdateDto projectUpdateDto);

    ProjectShortOutDto mapForEmployee(Project project);

    List<ProjectOutDto> map(List<Project> projectList);

    List<ProjectShortOutDto> mapForEmployee(List<Project> projectList);

    List<Project> mapUpdateList(List<ProjectUpdateDto> projectList);
}
