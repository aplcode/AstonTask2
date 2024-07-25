package ru.artemlychko.rest.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.model.EmployeeToProject;
import ru.artemlychko.rest.service.ProjectService;
import ru.artemlychko.rest.service.impl.ProjectServiceImpl;
import ru.artemlychko.rest.servlet.dto.ProjectInDto;
import ru.artemlychko.rest.servlet.dto.ProjectUpdateDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class ProjectServletTest {
    private static ProjectService mockProjectService;
    @InjectMocks
    private static ProjectServlet projectServlet;
    private static ProjectServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(ProjectService mock) {
        try {
            Field instance = ProjectServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (ProjectServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockProjectService = Mockito.mock(ProjectService.class);
        setMock(mockProjectService);
        projectServlet = new ProjectServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = ProjectServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockResponse);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("project/all").when(mockRequest).getPathInfo();

        projectServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockProjectService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("project/1").when(mockRequest).getPathInfo();

        projectServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockProjectService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("project/1").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockProjectService).findById(1L);

        projectServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("project/a").when(mockRequest).getPathInfo();

        projectServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {
        Mockito.doReturn("project/2").when(mockRequest).getPathInfo();

        projectServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockProjectService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteEmployeeFromProject() throws IOException, NotFoundException{

        Mockito.doReturn("project/1/deleteEmployee/1").when(mockRequest).getPathInfo();


        projectServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockProjectService).deleteEmployeeFromProject(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("project/a").when(mockRequest).getPathInfo();

        projectServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        String expectedName = "New project";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"name\":\"" + expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        projectServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<ProjectInDto> argumentCaptor = ArgumentCaptor.forClass(ProjectInDto.class);
        Mockito.verify(mockProjectService).save(argumentCaptor.capture());

        ProjectInDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        String expectedName = "Update project";

        Mockito.doReturn("project/").when(mockRequest).getPathInfo();
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\": 3,\"name\": \"" +
                        expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        projectServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<ProjectUpdateDto> argumentCaptor = ArgumentCaptor.forClass(ProjectUpdateDto.class);
        Mockito.verify(mockProjectService).update(argumentCaptor.capture());

        ProjectUpdateDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPutEmployeeToProject() throws IOException, NotFoundException {
        Long expectedProjectId = 1L;
        Long expectedEmployeeId = 1L;
        Mockito.doReturn("project/1/addEmployee/1").when(mockRequest).getPathInfo();
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();


        projectServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> argumentCaptor2 = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockProjectService).addEmployeeToProject(argumentCaptor1.capture(), argumentCaptor2.capture());

        Long resultEmployeeId = argumentCaptor1.getValue();
        Long resultProjectId = argumentCaptor2.getValue();
        Assertions.assertEquals(expectedEmployeeId, resultEmployeeId);
        Assertions.assertEquals(expectedProjectId, resultProjectId);
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        projectServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}

