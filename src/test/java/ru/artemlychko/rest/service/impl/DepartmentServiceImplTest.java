package ru.artemlychko.rest.service.impl;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.repository.DepartmentRepository;
import ru.artemlychko.rest.repository.impl.DepartmentRepositoryImpl;
import ru.artemlychko.rest.service.DepartmentService;
import ru.artemlychko.rest.servlet.dto.DepartmentInDto;
import ru.artemlychko.rest.servlet.dto.DepartmentOutDto;
import ru.artemlychko.rest.servlet.dto.DepartmentUpdateDto;
import ru.artemlychko.rest.servlet.dto.EmployeeUpdateDto;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

class DepartmentServiceImplTest {
    private static DepartmentService departmentService;
    private static DepartmentRepository mockDepartmentRepository;
    private static DepartmentRepositoryImpl oldInstance;

    private static void setMock(DepartmentRepository mock) {
        try {
            Field instance = DepartmentRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (DepartmentRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockDepartmentRepository = Mockito.mock(DepartmentRepository.class);
        setMock(mockDepartmentRepository);
        departmentService = DepartmentServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = DepartmentRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockDepartmentRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;

        DepartmentInDto departmentInDto = new DepartmentInDto("Department1");
        Department department = new Department(expectedId, "Department1", List.of());

        Mockito.doReturn(department).when(mockDepartmentRepository).save(Mockito.any(Department.class));

        DepartmentOutDto result = departmentService.save(departmentInDto);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        DepartmentUpdateDto departmentUpdateDto = new DepartmentUpdateDto(expectedId, "Department1");

        Mockito.doReturn(true).when(mockDepartmentRepository).existsById(Mockito.anyLong());

        departmentService.update(departmentUpdateDto);

        ArgumentCaptor<Department> argumentCaptor = ArgumentCaptor.forClass(Department.class);
        Mockito.verify(mockDepartmentRepository).update(argumentCaptor.capture());

        Department result = argumentCaptor.getValue();

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateWithIllegalArguments() throws IllegalArgumentException {
        DepartmentUpdateDto departmentUpdateDto = new DepartmentUpdateDto(null, null);

        Mockito.doReturn(true).when(mockDepartmentRepository).existsById(Mockito.any());

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {departmentService.update(departmentUpdateDto);
                }, "Illegal argument"
        );

        Assertions.assertEquals("Illegal argument", exception.getMessage());
    }

    @Test
    void updateNotFound() {
        DepartmentUpdateDto departmentUpdateDto = new DepartmentUpdateDto(1L, "Department1");

        Mockito.doReturn(false).when(mockDepartmentRepository).existsById(Mockito.anyLong());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    departmentService.update(departmentUpdateDto);
                }, "Not found"
        );

        Assertions.assertEquals("Department not found", exception.getMessage());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;

        Optional<Department> department = Optional.of(new Department(expectedId, "Department1", List.of()));

        Mockito.doReturn(true).when(mockDepartmentRepository).existsById(Mockito.anyLong());
        Mockito.doReturn(department).when(mockDepartmentRepository).findById(Mockito.anyLong());

        DepartmentOutDto result = departmentService.findById(expectedId);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void findByIdNotFound() {
        Optional<Department> department = Optional.empty();

        Mockito.doReturn(false).when(mockDepartmentRepository).existsById(Mockito.anyLong());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    departmentService.findById(1L);
                }, "Not found"
        );

        Assertions.assertEquals("Department not found", exception.getMessage());
    }

    @Test
    void findAll() {
        departmentService.findAll();
        Mockito.verify(mockDepartmentRepository).findAll();
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 1L;

        Mockito.doReturn(true).when(mockDepartmentRepository).existsById(Mockito.anyLong());
        departmentService.delete(expectedId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockDepartmentRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();

        Assertions.assertEquals(expectedId, result);
    }
}
