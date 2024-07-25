package ru.artemlychko.rest.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.service.ProjectService;
import ru.artemlychko.rest.service.impl.ProjectServiceImpl;
import ru.artemlychko.rest.servlet.dto.ProjectInDto;
import ru.artemlychko.rest.servlet.dto.ProjectOutDto;
import ru.artemlychko.rest.servlet.dto.ProjectUpdateDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/project/*"})
public class ProjectServlet extends HttpServlet {
    private final transient ProjectService projectService = ProjectServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public ProjectServlet() {
        this.objectMapper = new ObjectMapper();
    }

    private static void setJsonHeader(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private static String getJson(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader postData = req.getReader();
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
            if("all".equals(pathPart[1])) {
                List<ProjectOutDto> projectOutDtoList = projectService.findAll();
                response.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(projectOutDtoList);
            } else {
                Long projectId = Long.valueOf(pathPart[1]);
                ProjectOutDto projectOutDto = projectService.findById(projectId);
                response.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(projectOutDto);
            }
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
        Optional<ProjectInDto> projectResponse;
        try {
            projectResponse = Optional.ofNullable(objectMapper.readValue(json, ProjectInDto.class));
            ProjectInDto projectInDto = projectResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(projectService.save(projectInDto));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect request";
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
            Long projectId = Long.valueOf(pathPart[1]);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            if (pathPart.length > 3 && "deleteEmployee".equals(pathPart[2])) {
                    Long employeeId = Long.valueOf(pathPart[3]);
                    projectService.deleteEmployeeFromProject(projectId, employeeId);
            } else {
                projectService.delete(projectId);
            }
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setJsonHeader(response);
        String json = getJson(request);

        String responseAnswer = "";
        Optional<ProjectUpdateDto> projectResponse;
        try {
            String[] pathPart = request.getPathInfo().split("/");
            if (pathPart.length > 3 && "addEmployee".equals(pathPart[2])) {
                    Long projectId = Long.parseLong(pathPart[1]);
                    response.setStatus(HttpServletResponse.SC_OK);
                    Long employeeId = Long.parseLong(pathPart[3]);
                    projectService.addEmployeeToProject(projectId, employeeId);
            } else {
                projectResponse = Optional.ofNullable(objectMapper.readValue(json, ProjectUpdateDto.class));
                ProjectUpdateDto projectUpdateDto = projectResponse.orElseThrow(IllegalArgumentException::new);
                projectService.update(projectUpdateDto);
            }
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
