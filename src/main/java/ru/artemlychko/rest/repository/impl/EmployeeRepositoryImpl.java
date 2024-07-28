package ru.artemlychko.rest.repository.impl;

import ru.artemlychko.rest.db.ConnectionManager;
import ru.artemlychko.rest.db.ConnectionManagerImpl;
import ru.artemlychko.rest.exception.RepositoryException;
import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.model.EmployeeToProject;
import ru.artemlychko.rest.model.Project;
import ru.artemlychko.rest.repository.DepartmentRepository;
import ru.artemlychko.rest.repository.EmployeeRepository;
import ru.artemlychko.rest.repository.EmployeeToProjectRepository;
import ru.artemlychko.rest.repository.ProjectRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    private static final String SAVE_SQL = """
            INSERT INTO employees (employee_firstname, employee_lastname, department_id)
            VALUES (?, ? ,?) ;
            """;
    private static final String UPDATE_SQL = """
            UPDATE employees
            SET employee_firstname = ?,
                employee_lastname = ?,
                department_id =?
            WHERE employee_id = ?  ;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM employees
            WHERE employee_id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT employee_id, employee_firstname, employee_lastname, department_id FROM employees
            WHERE employee_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT employee_id, employee_firstname, employee_lastname, department_id FROM employees;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM employees
                        WHERE employee_id = ?
                        LIMIT 1);
            """;

    private static final String FIND_PROJECTS_BY_EMPLOYEE_ID_SQL = """
            SELECT projects.project_id, projects.project_name 
            FROM projects
                LEFT JOIN employees_projects
                    ON projects.project_id = employees_projects.project_id
            WHERE employee_id = ?;
            """;


    private static final String EMP_ID = "employee_id";
    private static EmployeeRepository instance;
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static final EmployeeToProjectRepository employeeToProjectRepository = EmployeeToProjectRepositoryImpl.getInstance();
    private static final ProjectRepository projectRepository = ProjectRepositoryImpl.getInstance();
    private static final DepartmentRepository departmentRepository = DepartmentRepositoryImpl.getInstance();

    private EmployeeRepositoryImpl() {
    }

    public static synchronized EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepositoryImpl();
        }
        return instance;
    }

    @Override
    public Employee save(Employee employee) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            if (employee.getDepartment() == null) {
                preparedStatement.setNull(3, Types.NULL);
            } else {
                preparedStatement.setLong(3, employee.getDepartment().getId());
            }
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                employee = new Employee(
                        resultSet.getLong(EMP_ID),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getDepartment(),
                        null
                );
            }
            saveProjectList(employee);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return employee;
    }

    private void saveProjectList(Employee employee) {
        if (employee.getProjectList() != null && !employee.getFirstName().isEmpty()) {
            List<Long> projectIdList = new ArrayList<>(
                    employee.getProjectList()
                            .stream()
                            .map(Project::getId)
                            .toList()
            );
            List<EmployeeToProject> existsProjectList = employeeToProjectRepository.findAllByEmployeeId(employee.getId());
            for (EmployeeToProject employeeToProject : existsProjectList) {
                if (!projectIdList.contains(employeeToProject.getProjectId())) {
                    employeeToProjectRepository.deleteById(employeeToProject.getId());
                }
                projectIdList.remove(employeeToProject.getProjectId());
            }
            for (Long projectId : projectIdList) {
                if (projectRepository.existsById(projectId)) {
                    EmployeeToProject employeeToProject = new EmployeeToProject(
                            null,
                            employee.getId(),
                            projectId
                    );
                    employeeToProjectRepository.save(employeeToProject);
                }
            }

        } else {
            employeeToProjectRepository.deleteByEmployeeId(employee.getId());
        }
    }

    @Override
    public void update(Employee employee) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            if (employee.getDepartment() == null) {
                preparedStatement.setNull(3, Types.NULL);
            } else {
                preparedStatement.setLong(3, employee.getDepartment().getId());
            }
            preparedStatement.setLong(4, employee.getId());

            preparedStatement.executeUpdate();
            saveProjectList(employee);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            employeeToProjectRepository.deleteByEmployeeId(id);

            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        Employee employee = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employee = createEmployee(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(employee);
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employeeList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeList.add(createEmployee(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return employeeList;
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

    @Override
    public List<Project> findProjectsByEmployeeId(Long employeeId) {
        List<Project> projectList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_PROJECTS_BY_EMPLOYEE_ID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Project project = new Project(
                        resultSet.getLong("project_id"),
                        resultSet.getString("project_name"),
                        null
                );
                projectList.add(project);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return projectList;
    }

    private Employee createEmployee(ResultSet resultSet) throws SQLException {
        Long employeeId = resultSet.getLong(EMP_ID);
        Department department = departmentRepository.findById(resultSet.getLong("department_id")).orElse(null);
        return new Employee(
                employeeId,
                resultSet.getString("employee_firstname"),
                resultSet.getString("employee_lastname"),
                department,
                findProjectsByEmployeeId(employeeId)

        );
    }
}
