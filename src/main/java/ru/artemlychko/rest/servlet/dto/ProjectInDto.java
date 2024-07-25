package ru.artemlychko.rest.servlet.dto;

public class ProjectInDto {
    private String name;

    public ProjectInDto(){
    }

    public ProjectInDto(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
