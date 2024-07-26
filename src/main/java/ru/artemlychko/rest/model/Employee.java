package ru.artemlychko.rest.model;


import java.util.ArrayList;
import java.util.List;

public class Employee {
    private Long id;
    private String firstName;
    private String lastName;
    private Department department;
    private List<Project> projectList;

    public Employee() {

    }

    public Employee(Long id, String firstName, String lastName, Department department, List<Project> projectList) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.projectList = projectList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Project> getProjectList() {
        if (projectList == null) {
            projectList = new ArrayList<>();
        }
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
