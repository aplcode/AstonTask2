package ru.artemlychko.rest.servlet.dto;

import ru.artemlychko.rest.model.Department;

public class EmployeeInDto {
    private String firstName;
    private String lastName;

    private Department department;

    public EmployeeInDto() {
    }

    public EmployeeInDto(String firstName, String lastName, Department department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Department getDepartment() {
        return department;
    }
}
