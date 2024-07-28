package ru.artemlychko.rest.repository;

import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.model.Project;

import java.util.List;

public interface EmployeeRepository extends Repository<Employee, Long> {
    List<Project> findProjectsByEmployeeId(Long employeeId);
}
