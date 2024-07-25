package ru.artemlychko.rest.repository;

import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.model.EmployeeToProject;
import ru.artemlychko.rest.model.Project;

import java.util.List;
import java.util.Optional;

public interface EmployeeToProjectRepository extends Repository<EmployeeToProject, Long> {
    boolean deleteByEmployeeId(Long employeeId);

    boolean deleteByProjectId(Long departmentId);

    List<EmployeeToProject> findAllByEmployeeId(Long employeeId);

    List<Project> findProjectsByEmployeeId(Long employeeId);

    List<EmployeeToProject> findAllByProjectId(Long projectId);

    List<Employee> findEmployeesByProjectId(Long projectId);

    Optional<EmployeeToProject> findByEmployeeIdAndProjectId(Long employeeId, Long projectId);
}
