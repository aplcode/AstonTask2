package ru.artemlychko.rest.servlet.dto;

public class DepartmentInDto {
    private String name;

    public DepartmentInDto(){
    }

    public DepartmentInDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
