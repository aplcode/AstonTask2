package ru.artemlychko.rest.servlet.dto;

import java.util.List;

public class EmployeeUpdateDto {
    private Long id;
    private String firstName;
    private String lastName;

    private DepartmentUpdateDto department;

    public EmployeeUpdateDto() {
    }

    public EmployeeUpdateDto(Long id, String firstName, String lastName, DepartmentUpdateDto department) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
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

}
