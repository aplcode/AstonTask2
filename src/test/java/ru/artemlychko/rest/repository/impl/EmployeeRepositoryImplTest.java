package ru.artemlychko.rest.repository.impl;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.repository.EmployeeRepository;
import ru.artemlychko.rest.util.PropertiesUtil;

import java.util.Optional;


@Testcontainers
@Tag("DockerRequired")
class EmployeeRepositoryImplTest {
    private static final String INIT_SQL = "sql/schema.sql";
    private static final int containerPort = 5432;
    private static final int localPort = 5432;
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("postgres")
            .withUsername(PropertiesUtil.getProperties("db.username"))
            .withPassword(PropertiesUtil.getProperties("db.password"))
            .withExposedPorts(containerPort)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(localPort), new ExposedPort(containerPort)))
            ))
            .withInitScript(INIT_SQL);
    public static EmployeeRepository employeeRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        employeeRepository = EmployeeRepositoryImpl.getInstance();
        jdbcDatabaseDelegate = new JdbcDatabaseDelegate(container, "");
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, INIT_SQL);
    }

    @Test
    void save() {
        String expectedFirstName = "FirstName";
        String expectedLastName = "LastName";

        Employee employee = new Employee(
                null,
                expectedFirstName,
                expectedLastName,
                null,
                null);
        employee = employeeRepository.save(employee);
        Optional<Employee> resultEmployee = employeeRepository.findById(employee.getId());

        Assertions.assertTrue(resultEmployee.isPresent());
        Assertions.assertEquals(expectedFirstName, resultEmployee.get().getFirstName());
        Assertions.assertEquals(expectedLastName, resultEmployee.get().getLastName());
    }

    @Test
    void update() {
        String expectedFirstName = "Update FirstName";
        String expectedLastName = "Update LastName";
        Long expectedDepartmentId = 1L;

        Employee updatedEmployee = employeeRepository.findById(3L).get();

        int projectListSize = updatedEmployee.getProjectList().size();
        Department oldDepartment = updatedEmployee.getDepartment();

        Assertions.assertNotEquals(expectedFirstName, updatedEmployee.getFirstName());
        Assertions.assertNotEquals(expectedLastName, updatedEmployee.getLastName());
        Assertions.assertNotEquals(expectedDepartmentId, updatedEmployee.getDepartment().getId());

        updatedEmployee.setFirstName(expectedFirstName);
        updatedEmployee.setLastName(expectedLastName);
        employeeRepository.update(updatedEmployee);

        Employee resultEmployee = employeeRepository.findById(3L).get();

        Assertions.assertEquals(expectedFirstName, resultEmployee.getFirstName());
        Assertions.assertEquals(expectedLastName, resultEmployee.getLastName());

        Assertions.assertEquals(projectListSize, updatedEmployee.getProjectList().size());
        Assertions.assertEquals(oldDepartment.getId(), updatedEmployee.getDepartment().getId());

        updatedEmployee.setDepartment(new Department(expectedDepartmentId, null, null));
        employeeRepository.update(updatedEmployee);
        resultEmployee = employeeRepository.findById(3L).get();

        Assertions.assertEquals(expectedDepartmentId, resultEmployee.getDepartment().getId());
    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = employeeRepository.findAll().size();

        Employee employee = new Employee(
                null,
                "Employee to delete firstname",
                "Employee to delete lastname",
                null,
                null
        );

        employee = employeeRepository.save(employee);

        boolean result = employeeRepository.deleteById(employee.getId());
        int employeesCount = employeeRepository.findAll().size();

        Assertions.assertEquals(expectedValue, result);
        Assertions.assertEquals(expectedSize, employeesCount);
    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<Employee> employee = employeeRepository.findById(expectedId);
        Assertions.assertEquals(expectedValue, employee.isPresent());
        employee.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @Test
    void findAll() {
        int expectedSize = 7;
        int resultSize = employeeRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void exitsById(Long employeeId, Boolean expectedValue) {
        boolean isUserExist = employeeRepository.existsById(employeeId);

        Assertions.assertEquals(expectedValue, isUserExist);
    }

    @DisplayName("Find projects by employee Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "3, 1",
            "6, 2",
            "1000, 0"
    })
    void findProjectsByEmployeeId(Long employeeId, int expectedSize) {
        int resultSize = employeeRepository.findProjectsByEmployeeId(employeeId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

}
