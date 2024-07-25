package ru.artemlychko.rest.model;

import ru.artemlychko.rest.repository.EmployeeToProjectRepository;
import ru.artemlychko.rest.repository.impl.EmployeeToProjectRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private static final EmployeeToProjectRepository employeeToProjectRepository = EmployeeToProjectRepositoryImpl.getInstance();
    private Long id;
    private String name;
    private List<Employee> employeeList;

    public Project() {
    }

    public Project(Long id, String name, List<Employee> employeeList) {
        this.id = id;
        this.name = name;
        this.employeeList = employeeList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Employee> getEmployeeList() {
        if (employeeList == null) {
            employeeList = employeeToProjectRepository.findEmployeesByProjectId(this.id);
        }
        return employeeList;
    }
}
