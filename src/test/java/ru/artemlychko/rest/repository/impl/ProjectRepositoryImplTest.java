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
import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.repository.ProjectRepository;
import ru.artemlychko.rest.util.PropertiesUtil;

import java.util.List;
import java.util.Optional;

@Testcontainers
@Tag("DockerRequired")
class ProjectRepositoryImplTest {
    private static final String INIT_SQL = "sql/schema.sql";
    public static ProjectRepository projectRepository;
    private static int containerPort = 5432;
    private static int localPort = 5432;
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
        projectRepository = ProjectRepositoryImpl.getInstance();
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
        String expectedName = "new Project";
        Project project = new Project(
                null,
                expectedName,
                null
        );
        project = projectRepository.save(project);
        Optional<Project> resultProject = projectRepository.findById(project.getId());

        Assertions.assertTrue(resultProject.isPresent());
        Assertions.assertEquals(expectedName, resultProject.get().getName());
    }

    @Test
    void update() {
        String expectedName = "Update project name";

        Project project = projectRepository.findById(2L).get();
        String oldName = project.getName();
        int expectedSizeEmployeeList = project.getEmployeeList().size();
        project.setName(expectedName);
        projectRepository.update(project);

        Project resultProject = projectRepository.findById(2L).get();
        int resultSizeEmployeeList = resultProject.getEmployeeList().size();

        Assertions.assertNotEquals(expectedName, oldName);
        Assertions.assertEquals(expectedName, resultProject.getName());
        Assertions.assertEquals(expectedSizeEmployeeList, resultSizeEmployeeList);
    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = projectRepository.findAll().size();

        Project tempProject = new Project(null, "New project", List.of());
        tempProject = projectRepository.save(tempProject);

        int resultSizeBefore = projectRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        boolean resultDelete = projectRepository.deleteById(tempProject.getId());
        int resultSizeAfter = projectRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, resultSizeAfter);
    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "3, true",
            "1000, false"
    })
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<Project> project = projectRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, project.isPresent());
        if (project.isPresent()) {
            Assertions.assertEquals(expectedId, project.get().getId());
        }
    }

    @Test
    void findAll() {
        int expectedSize = 4;
        int resultSize = projectRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void exitsById(Long projectId, Boolean expectedValue) {
        boolean isRoleExist = projectRepository.existsById(projectId);

        Assertions.assertEquals(expectedValue, isRoleExist);
    }

}
