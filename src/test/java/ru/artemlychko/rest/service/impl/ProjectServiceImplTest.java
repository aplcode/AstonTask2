package ru.artemlychko.rest.service.impl;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.model.EmployeeToProject;
import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.repository.EmployeeRepository;
import ru.artemlychko.rest.repository.EmployeeToProjectRepository;
import ru.artemlychko.rest.repository.ProjectRepository;
import ru.artemlychko.rest.repository.impl.DepartmentRepositoryImpl;
import ru.artemlychko.rest.repository.impl.EmployeeRepositoryImpl;
import ru.artemlychko.rest.repository.impl.EmployeeToProjectRepositoryImpl;
import ru.artemlychko.rest.repository.impl.ProjectRepositoryImpl;
import ru.artemlychko.rest.service.ProjectService;
import ru.artemlychko.rest.servlet.dto.ProjectInDto;
import ru.artemlychko.rest.servlet.dto.ProjectOutDto;
import ru.artemlychko.rest.servlet.dto.ProjectUpdateDto;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

class ProjectServiceImplTest {
    private static ProjectService projectService;
    private static ProjectRepository mockProjectRepository;
    private static EmployeeRepository mockEmployeeRepository;
    private static EmployeeToProjectRepository mockEmployeeToProjectRepository;
    private static ProjectRepositoryImpl oldProjectInstance;
    private static EmployeeRepositoryImpl oldEmployeeInstance;
    private static EmployeeToProjectRepositoryImpl oldEmployeeToProjectInstance;

    private static void setMock(ProjectRepository mock) {
        try {
            Field instance = ProjectRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldProjectInstance = (ProjectRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMock(EmployeeRepository mock) {
        try {
            Field instance = EmployeeRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldEmployeeInstance = (EmployeeRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMock(EmployeeToProjectRepository mock) {
        try {
            Field instance = EmployeeToProjectRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldEmployeeToProjectInstance = (EmployeeToProjectRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockProjectRepository = Mockito.mock(ProjectRepository.class);
        setMock(mockProjectRepository);
        mockEmployeeRepository = Mockito.mock(EmployeeRepository.class);
        setMock(mockEmployeeRepository);
        mockEmployeeToProjectRepository = Mockito.mock(EmployeeToProjectRepositoryImpl.class);
        setMock(mockEmployeeToProjectRepository);

        projectService = ProjectServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = ProjectRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldProjectInstance);

        instance = EmployeeRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldEmployeeInstance);

        instance = EmployeeToProjectRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldEmployeeToProjectInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockProjectRepository);
        Mockito.reset(mockEmployeeToProjectRepository);
        Mockito.reset(mockEmployeeRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;

        ProjectInDto projectInDto = new ProjectInDto("Department1");
        Project project = new Project(expectedId, "Project2", List.of());

        Mockito.doReturn(project).when(mockProjectRepository).save(Mockito.any(Project.class));

        ProjectOutDto result = projectService.save(projectInDto);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto(expectedId, "ProjectUpdate1");

        Mockito.doReturn(true).when(mockProjectRepository).existsById(Mockito.anyLong());

        projectService.update(projectUpdateDto);

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.verify(mockProjectRepository).update(argumentCaptor.capture());

        Project result = argumentCaptor.getValue();

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateNotFound() {
        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto(1L, "ProjectUpdate1");

        Mockito.doReturn(false).when(mockProjectRepository).existsById(Mockito.anyLong());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    projectService.update(projectUpdateDto);
                },
                "Not found"
        );
        Assertions.assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;

        Optional<Project> project = Optional.of(new Project(expectedId, "ProjectFound1", List.of()));

        Mockito.doReturn(true).when(mockProjectRepository).existsById(Mockito.anyLong());
        Mockito.doReturn(project).when(mockProjectRepository).findById(Mockito.anyLong());

        ProjectOutDto result = projectService.findById(expectedId);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void findByIdNotFound() {
        Optional<Project> project = Optional.empty();

        Mockito.doReturn(false).when(mockProjectRepository).existsById(Mockito.anyLong());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    projectService.findById(1L);
                }, "Not found"
        );

        Assertions.assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void findAll() {
        projectService.findAll();
        Mockito.verify(mockProjectRepository).findAll();
    }

    @Test
    void deleteById() throws NotFoundException {
        Long expectedId = 1L;

        Mockito.doReturn(true).when(mockProjectRepository).existsById(Mockito.anyLong());
        projectService.delete(expectedId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockProjectRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();

        Assertions.assertEquals(expectedId, result);
    }

    @Test
    void addEmployeeToProject() throws NotFoundException {
        Long expectedEmployeeId = 100L;
        Long expectedProjectId = 500L;

        Mockito.doReturn(true).when(mockEmployeeRepository).existsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockProjectRepository).existsById(Mockito.anyLong());

        projectService.addEmployeeToProject(expectedProjectId, expectedEmployeeId);

        ArgumentCaptor<EmployeeToProject> argumentCaptor = ArgumentCaptor.forClass(EmployeeToProject.class);
        Mockito.verify(mockEmployeeToProjectRepository).save(argumentCaptor.capture());
        EmployeeToProject result = argumentCaptor.getValue();

        Assertions.assertEquals(expectedEmployeeId, result.getEmployeeId());
        Assertions.assertEquals(expectedProjectId, result.getProjectId());
    }

    @Test
    void addNonExistentEmployeeToProject() {

        Mockito.doReturn(false).when(mockEmployeeRepository).existsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockProjectRepository).existsById(Mockito.anyLong());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {projectService.addEmployeeToProject(1L, 2L);
                }, "Employee not found"
        );

        Assertions.assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void deleteEmployeeFromProject() throws NotFoundException {
        Long expectedId = 100L;
        Optional<EmployeeToProject> link = Optional.of(new EmployeeToProject(expectedId, 1L, 2L));

        Mockito.doReturn(true).when(mockEmployeeRepository).existsById(Mockito.any());
        Mockito.doReturn(true).when(mockProjectRepository).existsById(Mockito.any());
        Mockito.doReturn(link).when(mockEmployeeToProjectRepository).findByEmployeeIdAndProjectId(Mockito.anyLong(), Mockito.anyLong());

        projectService.deleteEmployeeFromProject(1L, 1L);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockEmployeeToProjectRepository).deleteById(argumentCaptor.capture());
        Long result = argumentCaptor.getValue();


        Assertions.assertEquals(expectedId, result);
    }

    @Test
    void DeleteNonExistentEmployeeFromProject() {

        Mockito.doReturn(false).when(mockEmployeeRepository).existsById(Mockito.anyLong());
        Mockito.doReturn(true).when(mockProjectRepository).existsById(Mockito.anyLong());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {projectService.deleteEmployeeFromProject(1L, 2L);
                }, "Employee not found"
        );

        Assertions.assertEquals("Employee not found", exception.getMessage());
    }
}
