package ru.artemlychko.rest.repository.impl;

import ru.artemlychko.rest.db.ConnectionManager;
import ru.artemlychko.rest.db.ConnectionManagerImpl;
import ru.artemlychko.rest.exception.RepositoryException;
import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.repository.DepartmentRepository;
import ru.artemlychko.rest.repository.EmployeeToProjectRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentRepositoryImpl implements DepartmentRepository {
    private static final String SAVE_SQL = """
            INSERT INTO departments (department_name)
            VALUES (?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE departments
            SET department_name = ?
            WHERE department_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM departments
            WHERE department_id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT department_id, department_name FROM departments
            WHERE department_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT department_id, department_name FROM departments;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM departments
                        WHERE department_id = ?
                        LIMIT 1);
            """;
    private static final String DELETE_BY_DEPARTMENT_ID_SQL = """
            DELETE FROM employees
            WHERE department_id = ? ;
            """;

    private static final String FIND_EMPLOYEES_BY_DEPARTMENT_ID_SQL = """
            SELECT employee_id, employee_firstname, employee_lastname FROM employees
            WHERE department_id = ?;
            """;

    private static DepartmentRepository instance;
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static final EmployeeToProjectRepository employeeToProjectRepository = EmployeeToProjectRepositoryImpl.getInstance();

    private DepartmentRepositoryImpl() {
    }

    public static DepartmentRepository getInstance() {
        if (instance == null) {
            instance = new DepartmentRepositoryImpl();
        }
        return instance;
    }

    private Department createDepartment(ResultSet resultSet) throws SQLException {
        return new Department(
                resultSet.getLong("department_id"),
                resultSet.getString("department_name"),
                findEmployeesByDepartmentId(resultSet.getLong("department_id")));
    }

    @Override
    public Department save(Department department) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, department.getName());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                department = new Department(
                        resultSet.getLong("department_id"),
                        department.getName(),
                        null
                );
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return department;
    }


    @Override
    public void update(Department department) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setString(1, department.getName());
            preparedStatement.setLong(2, department.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult = true;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_BY_DEPARTMENT_ID_SQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement1.setLong(1, id);
            preparedStatement2.setLong(1, id);

            employeeToProjectRepository.deleteByEmployeeId(id);
            preparedStatement1.executeUpdate();
            deleteResult = preparedStatement2.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return deleteResult;
    }

    @Override
    public Optional<Department> findById(Long id) {
        Department department = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                department = createDepartment(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(department);
    }

    @Override
    public List<Department> findAll() {
        List<Department> departmentList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                departmentList.add(createDepartment(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return departmentList;
    }

    @Override
    public boolean existsById(Long id) {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return isExists;
    }

    public List<Employee> findEmployeesByDepartmentId(Long departmentId) {
        List<Employee> employeeList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_EMPLOYEES_BY_DEPARTMENT_ID_SQL)) {

            preparedStatement.setLong(1, departmentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                Employee employee = new Employee(
                        resultSet.getLong("employee_id"),
                        resultSet.getString("employee_firstname"),
                        resultSet.getString("employee_lastname"),
                        null,
                        null
                );
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return employeeList;
    }
}
