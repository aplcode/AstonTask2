package ru.artemlychko.rest.servlet.dto;

import java.util.List;

public class EmployeeOutDto {
    private Long id;
    private String firstName;
    private String lastName;

    private DepartmentShortOutDto department;
    private List<ProjectShortOutDto> projectList;

    public EmployeeOutDto() {
    }

    public EmployeeOutDto(Long id, String firstName, String lastName, DepartmentShortOutDto department, List<ProjectShortOutDto> projectList) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.projectList = projectList;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public DepartmentShortOutDto getDepartment() {
        return department;
    }

    public List<ProjectShortOutDto> getProjectList() {
        return projectList;
    }
}
