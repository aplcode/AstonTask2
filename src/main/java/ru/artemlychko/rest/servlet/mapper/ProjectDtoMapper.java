package ru.artemlychko.rest.servlet.mapper;

import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.servlet.dto.ProjectInDto;
import ru.artemlychko.rest.servlet.dto.ProjectOutDto;
import ru.artemlychko.rest.servlet.dto.ProjectUpdateDto;

import java.util.List;

public interface ProjectDtoMapper {
    Project map(ProjectInDto projectInDto);

    ProjectOutDto map(Project project);

    Project map(ProjectUpdateDto projectUpdateDto);

    List<ProjectOutDto> map(List<Project> projectList);

    List<Project> mapUpdateList(List<ProjectUpdateDto> projectList);
}
