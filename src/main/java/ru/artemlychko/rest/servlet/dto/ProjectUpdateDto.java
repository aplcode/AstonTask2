package ru.artemlychko.rest.servlet.dto;

public class ProjectUpdateDto {
    private Long id;
    private String name;

    public ProjectUpdateDto() {
    }

    public ProjectUpdateDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
