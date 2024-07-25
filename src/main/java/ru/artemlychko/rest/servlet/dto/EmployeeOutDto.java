package ru.artemlychko.rest.servlet.dto;

import java.util.List;

public class EmployeeOutDto {
    private Long id;
    private String firstName;
    private String lastName;

    private DepartmentOutDto department;
    private List<ProjectOutDto> projectList;

    public EmployeeOutDto() {
    }

    public EmployeeOutDto(Long id, String firstName, String lastName, DepartmentOutDto department, List<ProjectOutDto> projectList) {
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

    public DepartmentOutDto getDepartment() {
        return department;
    }

    public List<ProjectOutDto> getProjectList() {
        return projectList;
    }
}
