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
import ru.artemlychko.rest.repository.DepartmentRepository;
import ru.artemlychko.rest.util.PropertiesUtil;

import java.util.Optional;

@Testcontainers
@Tag("DockerRequired")
public class DepartmentRepositoryImplTest {
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
    public static DepartmentRepository departmentRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        departmentRepository = DepartmentRepositoryImpl.getInstance();
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
        String expectedName = "new Department";
        Department department = new Department(null, expectedName, null);
        department = departmentRepository.save(department);
        Optional<Department> resultDepartment = departmentRepository.findById(department.getId());

        Assertions.assertTrue(resultDepartment.isPresent());
        Assertions.assertEquals(expectedName, resultDepartment.get().getName());
    }

    @Test
    void update() {
        String expectedName = "Update department name";

        Department updateDepartment = departmentRepository.findById(3L).get();
        String oldDepartmentName = updateDepartment.getName();

        updateDepartment.setName(expectedName);
        departmentRepository.update(updateDepartment);

        Department department = departmentRepository.findById(3L).get();

        Assertions.assertNotEquals(expectedName, oldDepartmentName);
        Assertions.assertEquals(expectedName, department.getName());
    }

    @DisplayName("Delete by ID")
    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = departmentRepository.findAll().size();

        Department tempDepartment = new Department(null, "Departemnt for delete.", null);
        tempDepartment = departmentRepository.save(tempDepartment);

        boolean resultDelete = departmentRepository.deleteById(tempDepartment.getId());
        int roleListAfterSize = departmentRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, roleListAfterSize);
    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<Department> department = departmentRepository.findById(expectedId);
        Assertions.assertEquals(expectedValue, department.isPresent());
        if (department.isPresent()) {
            Assertions.assertEquals(expectedId, department.get().getId());
        }
    }

    @Test
    void findAll() {
        int expectedSize = 4;
        int resultSize = departmentRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void exitsById(Long departmentId, Boolean expectedValue) {
        boolean isRoleExist = departmentRepository.existsById(departmentId);

        Assertions.assertEquals(expectedValue, isRoleExist);
    }
}
