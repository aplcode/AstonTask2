package ru.artemlychko.rest.service.impl;

import ru.artemlychko.rest.model.EmployeeToProject;
import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.repository.EmployeeRepository;
import ru.artemlychko.rest.repository.EmployeeToProjectRepository;
import ru.artemlychko.rest.repository.ProjectRepository;
import ru.artemlychko.rest.repository.impl.EmployeeRepositoryImpl;
import ru.artemlychko.rest.repository.impl.EmployeeToProjectRepositoryImpl;
import ru.artemlychko.rest.repository.impl.ProjectRepositoryImpl;
import ru.artemlychko.rest.service.ProjectService;
import ru.artemlychko.rest.servlet.dto.ProjectInDto;
import ru.artemlychko.rest.servlet.dto.ProjectOutDto;
import ru.artemlychko.rest.servlet.dto.ProjectUpdateDto;
import ru.artemlychko.rest.servlet.mapper.ProjectDtoMapper;
import ru.artemlychko.rest.servlet.mapper.impl.ProjectDtoMapperImpl;

import java.util.List;

public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository = ProjectRepositoryImpl.getInstance();
    private final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();
    private final EmployeeToProjectRepository employeeToProjectRepository = EmployeeToProjectRepositoryImpl.getInstance();
    private static final ProjectDtoMapper projectDtoMapper = ProjectDtoMapperImpl.getInstance();
    private static ProjectService instance;

    private ProjectServiceImpl() {
    }

    public static synchronized ProjectService getInstance() {
        if (instance == null) {
            instance = new ProjectServiceImpl();
        }
        return instance;
    }

    private void checkExistProject(Long projectId) throws NotFoundException {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("Project not found");
        }
    }


    @Override
    public ProjectOutDto save(ProjectInDto projectInDto) {
        Project project = projectDtoMapper.map(projectInDto);
        project = projectRepository.save(project);
        return projectDtoMapper.map(project);
    }

    @Override
    public void update(ProjectUpdateDto projectUpdateDto) throws NotFoundException {
        checkExistProject(projectUpdateDto.getId());
        Project project = projectDtoMapper.map(projectUpdateDto);
        projectRepository.update(project);
    }

    @Override
    public ProjectOutDto findById(Long projectId) throws NotFoundException {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project not found"));
        return projectDtoMapper.map(project);
    }

    @Override
    public List<ProjectOutDto> findAll() {
        List<Project> projectList = projectRepository.findAll();
        return projectDtoMapper.map(projectList);
    }

    @Override
    public void delete(Long projectId) throws NotFoundException {
        checkExistProject(projectId);
        projectRepository.deleteById(projectId);
    }

    @Override
    public void deleteEmployeeFromProject(Long projectId, Long employeeId) throws NotFoundException {
        checkExistProject(projectId);
        if (employeeRepository.existsById(employeeId)) {
            EmployeeToProject linkEmployeeProject = employeeToProjectRepository.findByEmployeeIdAndProjectId(employeeId, projectId)
                    .orElseThrow(() -> new NotFoundException("Employee not found"));
            employeeToProjectRepository.deleteById(linkEmployeeProject.getId());
        } else {
            throw new NotFoundException("Employee not found");
        }
    }

    @Override
    public void addEmployeeToProject(Long projectId, Long employeeId) throws NotFoundException {
        checkExistProject(projectId);
        if (employeeRepository.existsById(employeeId)) {
            EmployeeToProject linkEmployeeProject = new EmployeeToProject(
                    null,
                    employeeId,
                    projectId
            );
            employeeToProjectRepository.save(linkEmployeeProject);
        } else {
            throw new NotFoundException("Employee not found");
        }
    }
}
