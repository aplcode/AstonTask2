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
import ru.artemlychko.rest.service.DepartmentService;
import ru.artemlychko.rest.service.impl.DepartmentServiceImpl;
import ru.artemlychko.rest.servlet.dto.DepartmentInDto;
import ru.artemlychko.rest.servlet.dto.DepartmentUpdateDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class DepartmentServletTest {
    private static DepartmentService mockDepartmentService;
    @InjectMocks
    private static DepartmentServlet departmentServlet;
    private static DepartmentServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(DepartmentService mock) {
        try {
            Field instance = DepartmentServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (DepartmentServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockDepartmentService = Mockito.mock(DepartmentService.class);
        setMock(mockDepartmentService);
        departmentServlet = new DepartmentServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = DepartmentServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockDepartmentService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("department/all").when(mockRequest).getPathInfo();

        departmentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockDepartmentService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("department/1").when(mockRequest).getPathInfo();

        departmentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockDepartmentService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("department/1").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockDepartmentService).findById(1L);

        departmentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("department/a").when(mockRequest).getPathInfo();

        departmentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {
        Mockito.doReturn("department/1").when(mockRequest).getPathInfo();
        Mockito.doNothing().when(mockDepartmentService).delete(Mockito.anyLong());

        departmentServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockDepartmentService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteNotFound() throws IOException, NotFoundException {
        Mockito.doReturn("department/1").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockDepartmentService).delete(1L);

        departmentServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockDepartmentService).delete(Mockito.anyLong());
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("department/a").when(mockRequest).getPathInfo();

        departmentServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        String expectedName = "New department HR";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"name\":\"" + expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        departmentServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<DepartmentInDto> argumentCaptor = ArgumentCaptor.forClass(DepartmentInDto.class);
        Mockito.verify(mockDepartmentService).save(argumentCaptor.capture());

        DepartmentInDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPostBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\":1}",
                null
        ).when(mockBufferedReader).readLine();

        departmentServlet.doPost(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        String expectedName = "Update department HR";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\": 4,\"name\": \"" +
                        expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        departmentServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<DepartmentUpdateDto> argumentCaptor = ArgumentCaptor.forClass(DepartmentUpdateDto.class);
        Mockito.verify(mockDepartmentService).update(argumentCaptor.capture());

        DepartmentUpdateDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        departmentServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPutNotFound() throws IOException, NotFoundException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\": 4,\"name\": \"HR\"}",
                null
        ).when(mockBufferedReader).readLine();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockDepartmentService)
                .update(Mockito.any(DepartmentUpdateDto.class));

        departmentServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockDepartmentService).update(Mockito.any(DepartmentUpdateDto.class));
    }
}
