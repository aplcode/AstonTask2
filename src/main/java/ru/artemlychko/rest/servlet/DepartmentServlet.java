package ru.artemlychko.rest.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.service.DepartmentService;
import ru.artemlychko.rest.service.impl.DepartmentServiceImpl;
import ru.artemlychko.rest.servlet.dto.DepartmentInDto;
import ru.artemlychko.rest.servlet.dto.DepartmentOutDto;
import ru.artemlychko.rest.servlet.dto.DepartmentUpdateDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;


@WebServlet(urlPatterns = {"/department/*"})
public class DepartmentServlet extends HttpServlet {
    private final transient DepartmentService departmentService = DepartmentServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public DepartmentServlet() {
        this.objectMapper = new ObjectMapper();
    }

    private static void setJsonHeader(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private static String getJson(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader postData = request.getReader();
        String line;
        while ((line = postData.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setJsonHeader(response);
        String responseAnswer = "";
        try {
            String[] pathPart = request.getPathInfo().split("/");
            if ("all".equals(pathPart[1])){
                List<DepartmentOutDto>  departmentOutDtoList = departmentService.findAll();
                response.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(departmentOutDtoList);
            } else {
                Long departmentId = Long.parseLong(pathPart[1]);
                DepartmentOutDto departmentOutDto = departmentService.findById(departmentId);
                response.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(departmentOutDto);
            }
        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request";
        }
        PrintWriter printWriter = response.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setJsonHeader(response);
        String responseAnswer = "";
        try {
            String[] pathPart = request.getPathInfo().split("/");
            Long departmentId = Long.parseLong(pathPart[1]);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            departmentService.delete(departmentId);
        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request";
        }
        PrintWriter printWriter = response.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setJsonHeader(response);
        String json = getJson(request);

        String responseAnswer = null;
        Optional<DepartmentInDto> departmentResponse;
        try {
            departmentResponse = Optional.ofNullable(objectMapper.readValue(json, DepartmentInDto.class));
            DepartmentInDto departmentInDto = departmentResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(departmentService.save(departmentInDto));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect request";
        }
        PrintWriter printWriter = response.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setJsonHeader(response);
        String json = getJson(request);

        String responseAnswer = "";
        Optional<DepartmentUpdateDto> departmentResponse;
        try {
            departmentResponse = Optional.ofNullable(objectMapper.readValue(json, DepartmentUpdateDto.class));
            DepartmentUpdateDto departmentUpdateDto = departmentResponse.orElseThrow(IllegalArgumentException::new);
            departmentService.update(departmentUpdateDto);
        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect request";
        }
        PrintWriter printWriter = response.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }
}
