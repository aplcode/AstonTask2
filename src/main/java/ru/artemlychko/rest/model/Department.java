package ru.artemlychko.rest.model;

import ru.artemlychko.rest.repository.EmployeeRepository;
import ru.artemlychko.rest.repository.impl.EmployeeRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private static final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();
    private Long id;
    private String name;
    private List<Employee> employeeList;

    public Department() {
    }

    public Department(Long id, String name, List<Employee> employeeList) {
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
            employeeList = employeeRepository.findAllByDepartmentId(this.id);
        }
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }
}
