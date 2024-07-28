package ru.artemlychko.rest.service.impl;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.repository.EmployeeRepository;
import ru.artemlychko.rest.repository.impl.EmployeeRepositoryImpl;
import ru.artemlychko.rest.service.EmployeeService;
import ru.artemlychko.rest.servlet.dto.DepartmentUpdateDto;
import ru.artemlychko.rest.servlet.dto.EmployeeInDto;
import ru.artemlychko.rest.servlet.dto.EmployeeOutDto;
import ru.artemlychko.rest.servlet.dto.EmployeeUpdateDto;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

class EmployeeServiceImplTest {
    private static EmployeeService employeeService;
    private static EmployeeRepository mockEmployeeRepository;
    private static Department department;
    private static EmployeeRepositoryImpl oldInstance;

    private static void setMock(EmployeeRepository mock) {
        try {
            Field instance = EmployeeRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (EmployeeRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        department = new Department(1L, "Department1", List.of());
        mockEmployeeRepository = Mockito.mock(EmployeeRepository.class);
        setMock(mockEmployeeRepository);
        employeeService = EmployeeServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception{
        Field instance = EmployeeRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockEmployeeRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;

        EmployeeInDto employeeInDto = new EmployeeInDto("fn1", "ln1", department);
        Employee employee = new Employee(expectedId, "fn1", "ln1", department, List.of());

        Mockito.doReturn(employee).when(mockEmployeeRepository).save(Mockito.any(Employee.class));

        EmployeeOutDto result = employeeService.save(employeeInDto);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        EmployeeUpdateDto employeeUpdateDto = new EmployeeUpdateDto(expectedId, "fn1", "ln1",
                new DepartmentUpdateDto(1L, "Department1"));

        Mockito.doReturn(true).when(mockEmployeeRepository).existsById(Mockito.any());

        employeeService.update(employeeUpdateDto);

        ArgumentCaptor<Employee> argumentCaptor = ArgumentCaptor.forClass(Employee.class);
        Mockito.verify(mockEmployeeRepository).update(argumentCaptor.capture());

        Employee result = argumentCaptor.getValue();

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateWithIllegalArguments() throws IllegalArgumentException {
        EmployeeUpdateDto employeeUpdateDto = new EmployeeUpdateDto(null, null, null, null);

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {employeeService.update(employeeUpdateDto);
                }, "Illegal argument"
        );

        Assertions.assertEquals("Illegal argument", exception.getMessage());
    }

    @Test
    void updateNotFound() {
        EmployeeUpdateDto employeeUpdateDto = new EmployeeUpdateDto(1L, "fn1", "ln1", null);

        Mockito.doReturn(false).when(mockEmployeeRepository).existsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {employeeService.update(employeeUpdateDto);
                }, "Not found"
        );

        Assertions.assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;

        Optional<Employee> employee = Optional.of(new Employee(expectedId, "fn1", "ln1", department, List.of()));

        Mockito.doReturn(true).when(mockEmployeeRepository).existsById(Mockito.any());
        Mockito.doReturn(employee).when(mockEmployeeRepository).findById(Mockito.anyLong());

        EmployeeOutDto result = employeeService.findById(expectedId);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void findByIdNotFound() {
        Optional<Employee> employee = Optional.empty();

        Mockito.doReturn(false).when(mockEmployeeRepository).existsById(Mockito.anyLong());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    employeeService.findById(1L);
                    }, "Not found"
        );

        Assertions.assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void findAll() {
        employeeService.findAll();
        Mockito.verify(mockEmployeeRepository).findAll();
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 1L;

        Mockito.doReturn(true).when(mockEmployeeRepository).existsById(Mockito.any());
        employeeService.delete(expectedId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockEmployeeRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();

        Assertions.assertEquals(expectedId, result);
    }
}
