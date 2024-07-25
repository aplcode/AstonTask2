package ru.artemlychko.rest.model;

public class EmployeeToProject {
    private Long id;
    private Long employeeId;
    private Long projectId;

    public EmployeeToProject() {
    }

    public EmployeeToProject(Long id, Long employeeId, Long projectId) {
        this.id = id;
        this.employeeId = employeeId;
        this.projectId = projectId;
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
