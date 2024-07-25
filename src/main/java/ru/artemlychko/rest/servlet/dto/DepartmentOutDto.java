package ru.artemlychko.rest.servlet.dto;

import java.util.List;

public class DepartmentOutDto {
    private Long id;
    private String name;
    private List<EmployeeShortOutDto> employeeList;


    public DepartmentOutDto(Long id, String name, List<EmployeeShortOutDto> employeeList) {
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

    public List<EmployeeShortOutDto> getEmployeeList(){
        return employeeList;
    }

    public void setEmployeeList(List<EmployeeShortOutDto> employeeList) {
        this.employeeList = employeeList;
    }
}
