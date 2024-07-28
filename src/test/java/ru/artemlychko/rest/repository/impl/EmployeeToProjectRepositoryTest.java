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
import ru.artemlychko.rest.model.EmployeeToProject;
import ru.artemlychko.rest.repository.EmployeeToProjectRepository;
import ru.artemlychko.rest.util.PropertiesUtil;

import java.util.Optional;

@Testcontainers
@Tag("DockerRequired")
class EmployeeToProjectRepositoryTest {
    private static final String INIT_SQL = "sql/schema.sql";
    public static EmployeeToProjectRepository employeeToProjectRepository;
    private static int containerPort = 5432;
    private static int localPort = 8081;

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

    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        employeeToProjectRepository = EmployeeToProjectRepositoryImpl.getInstance();
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
        Long expectedEmployeeId = 1L;
        Long expectedProjectId = 2L;
        EmployeeToProject link = new EmployeeToProject(
                null,
                expectedEmployeeId,
                expectedProjectId
        );
        link = employeeToProjectRepository.save(link);
        Optional<EmployeeToProject> resultLink = employeeToProjectRepository.findById(link.getId());

        Assertions.assertTrue(resultLink.isPresent());
        Assertions.assertEquals(expectedEmployeeId, resultLink.get().getEmployeeId());
        Assertions.assertEquals(expectedProjectId, resultLink.get().getProjectId());
    }

    @Test
    void update() {
        Long expectedEmployeeId = 1L;
        Long expectedProjectId = 2L;

        EmployeeToProject link = employeeToProjectRepository.findById(2L).get();

        Long oldProjectId = link.getProjectId();
        Long oldEmployeeId = link.getEmployeeId();

        Assertions.assertNotEquals(expectedEmployeeId, oldEmployeeId);
        Assertions.assertNotEquals(expectedProjectId, oldProjectId);

        link.setEmployeeId(expectedEmployeeId);
        link.setProjectId(expectedProjectId);

        employeeToProjectRepository.update(link);

        EmployeeToProject resultLink = employeeToProjectRepository.findById(2L).get();

        Assertions.assertEquals(link.getId(), resultLink.getId());
        Assertions.assertEquals(expectedEmployeeId, resultLink.getEmployeeId());
        Assertions.assertEquals(expectedProjectId, resultLink.getProjectId());
    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = employeeToProjectRepository.findAll().size();

        EmployeeToProject link = new EmployeeToProject(null, 1L, 2L);
        link = employeeToProjectRepository.save(link);

        int resultSizeBefore = employeeToProjectRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        boolean resltDeleted = employeeToProjectRepository.deleteById(link.getId());

        int resultSizeAfter = employeeToProjectRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resltDeleted);
        Assertions.assertEquals(expectedSize, resultSizeAfter);
    }

    @DisplayName("Delete by EmployeeId.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "100, false"
    })
    void deleteByEmployeeId(Long expectedEmployeeId, Boolean expectedValue) {
        int beforeSize = employeeToProjectRepository.findAllByEmployeeId(expectedEmployeeId).size();
        Boolean resultDelete = employeeToProjectRepository.deleteByEmployeeId(expectedEmployeeId);

        int afterDelete = employeeToProjectRepository.findAllByEmployeeId(expectedEmployeeId).size();

        Assertions.assertEquals(expectedValue, resultDelete);
        if (beforeSize != 0) {
            Assertions.assertNotEquals(beforeSize, afterDelete);
        }
    }

    @DisplayName("Delete by Project Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "100, false"
    })
    void deleteByProjectId(Long expectedProjectId, Boolean expectedValue) {
        int beforeSize = employeeToProjectRepository.findAllByProjectId(expectedProjectId).size();
        Boolean resultDelete = employeeToProjectRepository.deleteByProjectId(expectedProjectId);

        int afterDelete = employeeToProjectRepository.findAllByProjectId(expectedProjectId).size();

        Assertions.assertEquals(expectedValue, resultDelete);
        if (beforeSize != 0) {
            Assertions.assertNotEquals(beforeSize, afterDelete);
        }
    }

    @DisplayName("Find by Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true, 1, 1",
            "2, true, 2, 1",
            "100, false, 0, 0"
    })
    void findById(Long expectedId, Boolean expectedValue, Long expectedEmployeeId, Long expectedProjectId) {
        Optional<EmployeeToProject> link = employeeToProjectRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, link.isPresent());
        if (link.isPresent()) {
            Assertions.assertEquals(expectedId, link.get().getId());
            Assertions.assertEquals(expectedEmployeeId, link.get().getEmployeeId());
            Assertions.assertEquals(expectedProjectId, link.get().getProjectId());
        }
    }

    @Test
    void findAll() {
        int expectedSize = 8;
        int resultSize = employeeToProjectRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exists by Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "2, true",
            "1000, false"
    })
    void exitsById(Long expectedId, Boolean expectedValue) {
        Boolean resultValue = employeeToProjectRepository.existsById(expectedId);

        Assertions.assertEquals(expectedValue, resultValue);
    }

    @DisplayName("Find all by employee Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 1",
            "6, 2",
            "1000, 0"
    })
    void findAllByUserId(Long employeeId, int expectedSize) {
        int resultSize = employeeToProjectRepository.findAllByEmployeeId(employeeId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Find links by employee Id and Project Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 1, true",
            "1, 4, false"
    })
    void findByEmployeeIdAndProjectId(Long employeeId, Long projectId, Boolean expectedValue) {
        Optional<EmployeeToProject> link = employeeToProjectRepository.findByEmployeeIdAndProjectId(employeeId, projectId);

        Assertions.assertEquals(expectedValue, link.isPresent());
    }
}

