package ru.artemlychko.rest.servlet.dto;

public class DepartmentShortOutDto {
    private Long id;
    private String name;


    public DepartmentShortOutDto(Long id, String name) {
        this.id = id;
        this.name = name;
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
}