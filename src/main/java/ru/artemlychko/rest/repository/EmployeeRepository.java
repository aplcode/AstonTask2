package ru.artemlychko.rest.repository;

import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.model.Employee;

import java.util.List;

public interface EmployeeRepository extends Repository<Employee, Long> {
    List<Employee> findAllByDepartmentId(Long departmentId);
}
