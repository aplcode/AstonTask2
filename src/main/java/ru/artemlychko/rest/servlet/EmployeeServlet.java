package ru.artemlychko.rest.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.service.EmployeeService;
import ru.artemlychko.rest.service.impl.EmployeeServiceImpl;
import ru.artemlychko.rest.servlet.dto.EmployeeInDto;
import ru.artemlychko.rest.servlet.dto.EmployeeOutDto;
import ru.artemlychko.rest.servlet.dto.EmployeeUpdateDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/employee/*"})
public class EmployeeServlet extends HttpServlet {
    private final transient EmployeeService employeeService = EmployeeServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public EmployeeServlet() {
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
            if ("all".equals(pathPart[1])) {
                List<EmployeeOutDto> employeeOutDtoList = employeeService.findAll();
                response.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(employeeOutDtoList);
            } else {
                try {
                    Long employeeId = Long.parseLong(pathPart[1]);
                    EmployeeOutDto employeeOutDto = employeeService.findById(employeeId);
                    response.setStatus(HttpServletResponse.SC_OK);
                    responseAnswer = objectMapper.writeValueAsString(employeeOutDto);
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseAnswer = e.getMessage();
                }

            }
        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = e.getMessage();
            e.printStackTrace();
        }
        PrintWriter printWriter = response.getWriter();
        printWriter.print(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setJsonHeader(response);
        String responseAnswer = "";
        try {
            String[] pathPart = request.getPathInfo().split("/");
            Long employeeId = Long.parseLong(pathPart[1]);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            employeeService.delete(employeeId);
        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request";
        }
        PrintWriter printWriter = response.getWriter();
        printWriter.print(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setJsonHeader(response);
        String json = getJson(request);

        String responseAnswer = null;
        Optional<EmployeeInDto> employeeResponse;
        try {
            employeeResponse = Optional.ofNullable(objectMapper.readValue(json, EmployeeInDto.class));
            EmployeeInDto employeeInDto = employeeResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(employeeService.save(employeeInDto));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect request";
        }
        PrintWriter printWriter = response.getWriter();
        printWriter.print(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setJsonHeader(response);
        String json = getJson(request);

        String responseAnswer = "";
        Optional<EmployeeUpdateDto> employeeResponse;
        try {
            employeeResponse = Optional.ofNullable(objectMapper.readValue(json, EmployeeUpdateDto.class));
            EmployeeUpdateDto employeeUpdateDto = employeeResponse.orElseThrow(IllegalArgumentException::new);
            employeeService.update(employeeUpdateDto);
        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect request";
        }
        PrintWriter printWriter = response.getWriter();
        printWriter.print(responseAnswer);
        printWriter.flush();
    }
}
