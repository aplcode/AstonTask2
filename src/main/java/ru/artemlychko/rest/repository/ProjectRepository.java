package ru.artemlychko.rest.repository;

import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.model.Project;

import java.util.List;

public interface ProjectRepository extends Repository<Project, Long>{
    List<Employee> findEmployeesByProjectId(Long projectId);
}
