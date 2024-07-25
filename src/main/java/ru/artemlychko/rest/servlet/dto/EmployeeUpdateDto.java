package ru.artemlychko.rest.servlet.dto;

import java.util.List;

public class EmployeeUpdateDto {
    private Long id;
    private String firstName;
    private String lastName;

    private DepartmentUpdateDto department;
    private List<ProjectUpdateDto> projectList;

    public EmployeeUpdateDto() {
    }

    public EmployeeUpdateDto(Long id, String firstName, String lastName, DepartmentUpdateDto department, List<ProjectUpdateDto> projectList) {
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

    public DepartmentUpdateDto getDepartment() {
        return department;
    }

    public List<ProjectUpdateDto> getProjectList() {
        return projectList;
    }
}
